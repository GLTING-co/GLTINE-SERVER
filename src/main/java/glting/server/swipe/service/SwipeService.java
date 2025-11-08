package glting.server.swipe.service;


import glting.server.exception.NotFoundException;
import glting.server.exception.code.ExceptionCodeMapper;
import glting.server.swipe.entity.SwipeEntity;
import glting.server.swipe.repository.SwipeRepository;
import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static glting.server.exception.code.ExceptionCodeMapper.getCode;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final UserRepository userRepository;

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

}
