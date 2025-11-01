package glting.server.swipe.repository;

import glting.server.swipe.entity.SwipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SwipeRepository extends JpaRepository<SwipeEntity, Long> {

    // 내가 스와이프한(to_user_seq) 사람 ID 목록
    @Query("SELECT s.toUserSeq FROM SwipeEntity s WHERE s.fromUserSeq = :userSeq")
    List<Long> findSwipedUserIds(@Param("userSeq") Long userSeq);
}
