package glting.server.chat.repository;

import glting.server.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageEntity, String> {
    @Query("""
            SELECT cm.message
            FROM ChatMessageEntity cm
            WHERE cm.chatRoomEntity.chatRoomSeq = :chatRoomSeq AND cm.deleted = false
            ORDER BY cm.createdAt DESC
            LIMIT 1
            """)
    String findRecentMessageByChatRoomSeq(@Param("chatRoomSeq") String chatRoomSeq);
}
