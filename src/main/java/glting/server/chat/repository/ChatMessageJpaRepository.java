package glting.server.chat.repository;

import glting.server.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    @Query("""
            SELECT cm
            FROM ChatMessageEntity cm
            WHERE cm.chatMessageSeq = :chatMessageSeq AND cm.deleted = false
            """)
    Optional<ChatMessageEntity> findByChatMessageSeq(@Param("chatMessageSeq") String chatMessageSeq);

    @Modifying
    @Query("""
            UPDATE ChatMessageEntity cm
            SET cm.isRead = true
            WHERE cm.chatRoomEntity.chatRoomSeq = :chatRoomSeq
            AND cm.createdAt <= (
                SELECT cm2.createdAt
                FROM ChatMessageEntity cm2
                WHERE cm2.chatMessageSeq = :chatMessageSeq
            )
            AND cm.deleted = false
            AND cm.isRead = false
            """)
    int markMessagesAsReadBefore(@Param("chatRoomSeq") String chatRoomSeq, @Param("chatMessageSeq") String chatMessageSeq);
}
