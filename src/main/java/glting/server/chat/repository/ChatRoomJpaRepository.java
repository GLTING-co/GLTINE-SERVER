package glting.server.chat.repository;

import glting.server.chat.entity.ChatRoomEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomEntity, String> {
    @Query("""
            SELECT DISTINCT cr
            FROM ChatRoomEntity cr
            JOIN FETCH cr.userA a
            JOIN FETCH cr.userB b
            WHERE (cr.userA.userSeq = :userSeq OR cr.userB.userSeq = :userSeq) AND cr.deleted = false
            ORDER BY cr.createdAt DESC
            """)
    List<ChatRoomEntity> findAllByUserSeq(@Param("userSeq") Long userSeq, Pageable pageable);

    @Query("""
            SELECT cr
            FROM ChatRoomEntity cr
            JOIN FETCH cr.userA a
            JOIN FETCH cr.userB b
            WHERE cr.chatRoomSeq = :chatRoomSeq AND cr.deleted = false
            ORDER BY cr.createdAt DESC
            """)
    ChatRoomEntity findByChatRoomSeq(@Param("chatRoomSeq") String chatRoomSeq);
}
