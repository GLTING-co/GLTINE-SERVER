package glting.server.swipe.repository;

import glting.server.swipe.entity.SwipeEntity;
import glting.server.users.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SwipeRepository {

    private final SwipeJpaRepository swipeJpaRepository;

    public void saveSwipe(SwipeEntity swipe) {
        swipeJpaRepository.save(swipe);
    }

    public boolean isMatch(Long fromUserSeq, Long toUserSeq) {
        return swipeJpaRepository.isMatched(fromUserSeq, toUserSeq);
    }
}
