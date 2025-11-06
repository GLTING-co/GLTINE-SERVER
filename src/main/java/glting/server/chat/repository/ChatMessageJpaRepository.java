package glting.server.chat.repository;

import glting.server.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageEntity, String> {
    @Query("""
            SELECT cm
            FROM ChatMessageEntity cm
            JOIN FETCH cm.senderEntity s
            JOIN FETCH cm.receiverEntity r
            WHERE cm.chatRoomEntity.chatRoomSeq = :chatRoomSeq AND cm.deleted = false
            ORDER BY cm.createdAt DESC
            """)
    List<ChatMessageEntity> findAllByChatRoomSeq(@Param("chatRoomSeq") String chatRoomSeq);
}
