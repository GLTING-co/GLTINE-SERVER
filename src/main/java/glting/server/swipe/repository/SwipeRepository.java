package glting.server.swipe.repository;

import glting.server.swipe.entity.SwipeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SwipeRepository {

    private final SwipeJpaRepository swipeJpaRepository;

    public void saveSwipe(SwipeEntity swipe){
        swipeJpaRepository.save(swipe);
    }
}
