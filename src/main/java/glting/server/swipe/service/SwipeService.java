package glting.server.swipe.service;


import glting.server.chat.entity.ChatRoomEntity;
import glting.server.chat.repository.ChatRoomRepository;
import glting.server.exception.NotFoundException;
import glting.server.exception.code.ExceptionCodeMapper;
import glting.server.match.entity.MatchEntity;
import glting.server.match.repository.MatchRepository;
import glting.server.swipe.entity.SwipeEntity;
import glting.server.swipe.repository.SwipeRepository;
import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
        swipeRepository.saveSwipe(SwipeEntity.builder()
                .fromUserSeq(fromUserEntity)
                .toUserSeq(toUserEntity)
                .liked(true)
                .build());

        // 2. 매칭 여부 확인
        SwipeEntity matchedSwipe = swipeRepository.findByFromUserSeqAndToUserSeq(fromUserEntity, toUserEntity)
                .orElse(new SwipeEntity());

        boolean liked = Boolean.TRUE.equals(matchedSwipe.getLiked());

        if (liked) {

            matchRepository.saveMatch(MatchEntity.builder()
                    .userA(toUserEntity)
                    .userB(fromUserEntity)
                    .matchedAt(LocalDateTime.now())
                    .build());

            chatRoomRepository.save(
                    ChatRoomEntity.builder()
                            .userA(fromUserEntity)
                            .userB(toUserEntity)
                            .build()
            );
        }

        return new MatchedResponse(liked);
    }

}
