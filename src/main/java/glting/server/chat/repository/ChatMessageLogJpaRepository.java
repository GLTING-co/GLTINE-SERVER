package glting.server.chat.repository;

import glting.server.chat.entity.ChatMessageLogEntity;
import glting.server.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageLogJpaRepository extends JpaRepository<ChatMessageLogEntity, String> {
    Optional<ChatMessageLogEntity> findByChatRoomEntity(ChatRoomEntity chatRoomEntity);
    
    List<ChatMessageLogEntity> findByChatRoomEntityIn(List<ChatRoomEntity> chatRooms);
}
