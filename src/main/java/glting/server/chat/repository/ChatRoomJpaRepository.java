package glting.server.chat.repository;

import glting.server.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomEntity, String> {

    @Query("""
            SELECT cr
            FROM ChatRoomEntity cr
            JOIN FETCH cr.userA a
            JOIN FETCH cr.userB b
            WHERE cr.chatRoomSeq = :chatRoomSeq AND cr.deleted = false
            ORDER BY cr.createdAt DESC
            """)
    Optional<ChatRoomEntity> findByChatRoomSeq(@Param("chatRoomSeq") String chatRoomSeq);
}
