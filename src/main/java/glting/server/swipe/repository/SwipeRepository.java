package glting.server.swipe.repository;

import glting.server.swipe.entity.SwipeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SwipeRepository {

    private final SwipeJpaRepository swipeJpaRepository;

    /**
     * 스와이프 엔티티를 저장합니다.
     *
     * @param swipe 저장할 스와이프 엔티티
     */
    public void saveSwipe(SwipeEntity swipe) {
        swipeJpaRepository.save(swipe);
    }

    /**
     * 두 사용자 간에 서로 좋아요를 보냈는지 확인합니다.
     *
     * @param fromUserSeq 사용자 A 고유 식별자(PK)
     * @param toUserSeq   사용자 B 고유 식별자(PK)
     * @return 서로 좋아요를 보낸 경우 true, 아니면 false
     */
    public boolean isMatch(Long fromUserSeq, Long toUserSeq) {
        return swipeJpaRepository.isMatched(fromUserSeq, toUserSeq);
    }
}
