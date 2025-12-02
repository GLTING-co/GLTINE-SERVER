package glting.server.swipe.service;


import glting.server.chat.entity.ChatRoomEntity;
import glting.server.chat.repository.ChatRoomRepository;
import glting.server.exception.ConflictException;
import glting.server.exception.NotFoundException;
import glting.server.exception.ServerException;
import glting.server.exception.code.ExceptionCodeMapper;
import glting.server.match.entity.MatchEntity;
import glting.server.match.repository.MatchRepository;
import glting.server.swipe.entity.SwipeEntity;
import glting.server.swipe.repository.SwipeRepository;
import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static glting.server.exception.code.ExceptionCodeMapper.getCode;
import static glting.server.swipe.controller.response.SwipeResponse.*;

@Service
@RequiredArgsConstructor
public class SwipeService {
    private final SwipeRepository swipeRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedissonClient redissonClient;

    /**
     * 사용자가 다른 사용자에게 싫어요를 보냅니다.
     *
     * @param fromUserSeq 보내는 사용자 고유 식별자(PK)
     * @param toUserSeq   받는 사용자 고유 식별자(PK)
     */
    @Transactional
    public void dislike(Long fromUserSeq, Long toUserSeq) {

        UserEntity fromUserEntity = userRepository.findByUserSeq(fromUserSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionCodeMapper.ExceptionType.NOT_FOUND)
                ));

        UserEntity toUserEntity = userRepository.findByUserSeq(toUserSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionCodeMapper.ExceptionType.NOT_FOUND)
                ));

        SwipeEntity swipeEntity = SwipeEntity.builder()
                .fromUserSeq(fromUserEntity)
                .toUserSeq(toUserEntity)
                .liked(false)
                .build();

        swipeRepository.saveSwipe(swipeEntity);

    }

    /**
     * 사용자가 다른 사용자에게 좋아요를 보냅니다.
     * 상대방도 좋아요를 보낸 경우 매칭이 성사되고 채팅방이 생성됩니다.
     *
     * @param fromUserSeq 보내는 사용자 고유 식별자(PK)
     * @param toUserSeq   받는 사용자 고유 식별자(PK)
     * @return 매칭 여부 (true: 매칭 성사, false: 매칭 실패)
     */
    @Transactional
    public MatchedResponse like(Long fromUserSeq, Long toUserSeq) {

        // 0. 유효성 검사
        UserEntity fromUserEntity = userRepository.findByUserSeq(fromUserSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionCodeMapper.ExceptionType.NOT_FOUND)
                ));

        UserEntity toUserEntity = userRepository.findByUserSeq(toUserSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionCodeMapper.ExceptionType.NOT_FOUND)
                ));

        // 1. 좋아요 저장
        try {
            swipeRepository.saveSwipe(SwipeEntity.builder()
                    .fromUserSeq(fromUserEntity)
                    .toUserSeq(toUserEntity)
                    .liked(true)
                    .build());
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    HttpStatus.CONFLICT.value(),
                    "이미 Swipe 정보가 존재합니다.",
                    getCode("이미 Swipe 정보가 존재합니다.", ExceptionCodeMapper.ExceptionType.CONFLICT)
            );
        } catch (Exception e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionCodeMapper.ExceptionType.SERVER)
            );
        }

        // 2. 매칭 여부 확인
        boolean liked = swipeRepository.isMatch(fromUserSeq, toUserSeq);

        if (!liked) {
            return new MatchedResponse(false);
        }

        UserEntity userA = fromUserEntity.getUserSeq() < toUserEntity.getUserSeq() ? fromUserEntity : toUserEntity;
        UserEntity userB = fromUserEntity.getUserSeq() < toUserEntity.getUserSeq() ? toUserEntity : fromUserEntity;

        // 3. 매치 저장
        try {
            matchRepository.saveMatch(MatchEntity.builder()
                    .userA(userA)
                    .userB(userB)
                    .matchedAt(LocalDateTime.now())
                    .build());

        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    HttpStatus.CONFLICT.value(),
                    "이미 매치된 정보가 존재합니다.",
                    getCode("이미 매치된 정보가 존재합니다.", ExceptionCodeMapper.ExceptionType.CONFLICT)
            );
        } catch (Exception e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionCodeMapper.ExceptionType.SERVER)
            );
        }

        String lockKey = "match:" + userA.getUserSeq() + ":" + userB.getUserSeq();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            lock.lock();

            chatRoomRepository.findByUserASeqAndUserBSeq(userA.getUserSeq(), userB.getUserSeq())
                    .ifPresent(chatRoom -> {
                        throw new ConflictException(
                                HttpStatus.CONFLICT.value(),
                                "이미 생성된 채팅방 입니다.",
                                getCode("이미 생성된 채팅방 입니다.", ExceptionCodeMapper.ExceptionType.CONFLICT)
                        );
                    });

            chatRoomRepository.save(
                    ChatRoomEntity.builder()
                            .unReadNum(0L)
                            .userA(userA)
                            .userB(userB)
                            .deleted(false)
                            .version(0)
                            .build()
            );
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return new MatchedResponse(true);
    }
}
