package glting.server.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import glting.server.chat.entity.ChatMessageLogEntity;
import glting.server.chat.entity.ChatRoomEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class ChatMessageLogRepository {
    private final JPAQueryFactory queryFactory;
    private final ChatMessageLogJpaRepository messageReadLogJpaRepository;

    public ChatMessageLogEntity save(ChatMessageLogEntity chatMessageLogEntity) {
        return messageReadLogJpaRepository.save(chatMessageLogEntity);
    }

    public Optional<ChatMessageLogEntity> findByChatRoomEntity(ChatRoomEntity chatRoomEntity) {
        return messageReadLogJpaRepository.findByChatRoomEntity(chatRoomEntity);
    }

    /**
     * 여러 채팅방의 로그를 일괄 조회합니다.
     *
     * @param chatRooms 채팅방 엔티티 목록
     * @return 채팅방 SEQ를 키로 하는 로그 Map (로그가 없으면 포함되지 않음)
     */
    public Map<String, ChatMessageLogEntity> findByChatRoomEntities(List<ChatRoomEntity> chatRooms) {
        if (chatRooms.isEmpty()) {
            return Map.of();
        }
        
        return messageReadLogJpaRepository.findByChatRoomEntityIn(chatRooms)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        log -> log.getChatRoomEntity().getChatRoomSeq(),
                        log -> log,
                        (existing, replacement) -> existing
                ));
    }
}
