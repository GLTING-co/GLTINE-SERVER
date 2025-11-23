package glting.server.swipe.repository;

import glting.server.swipe.entity.SwipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SwipeJpaRepository extends JpaRepository<SwipeEntity, Long> {
    @Query("""
            SELECT CASE WHEN COUNT(s1) > 0 THEN true ELSE false END
            FROM SwipeEntity s1
            JOIN SwipeEntity s2
              ON s1.fromUserSeq =:toUserSeq
             AND s1.toUserSeq =:fromUserSeq
            WHERE s1.liked = true
              AND s2.liked = true
              AND s1.fromUserSeq = :user1
              AND s1.toUserSeq = :user2
            """)
    boolean isMatched(@Param("fromUserSeq") Long fromUserSeq, @Param("toUserSeq") Long toUserSeq);
}
