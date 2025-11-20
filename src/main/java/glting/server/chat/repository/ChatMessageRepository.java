package glting.server.chat.repository;

import glting.server.chat.entity.ChatMessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepository {
    private final ChatMessageJpaRepository chatMessageJpaRepository;

    /**
     * 채팅방 고유 식별자로 채팅 메시지 목록을 페이징하여 조회합니다.
     *
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @param pageable    페이징 정보
     * @return 채팅 메시지 엔티티 리스트
     */
    public List<ChatMessageEntity> findAllByChatRoomSeq(String chatRoomSeq, Pageable pageable) {
        return chatMessageJpaRepository.findAllByChatRoomSeq(chatRoomSeq, pageable);
    }

    /**
     * 채팅 메시지 엔티티를 저장합니다.
     *
     * @param chatMessageEntity 저장할 채팅 메시지 엔티티
     * @return 저장된 채팅 메시지 엔티티
     */
    public ChatMessageEntity save(ChatMessageEntity chatMessageEntity) {
        return chatMessageJpaRepository.save(chatMessageEntity);
    }
}
