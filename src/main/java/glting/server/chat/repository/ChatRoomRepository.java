package glting.server.chat.repository;

import glting.server.chat.entity.ChatRoomEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    /**
     * 호스트 사용자 고유 식별자로 채팅방 목록을 조회합니다.
     * JOIN FETCH를 사용하여 guestEntity와 hostEntity를 함께 로드합니다.
     *
     * @param hostSeq 호스트 사용자 고유 식별자(PK)
     * @return 채팅방 엔티티 목록 (생성일시 내림차순 정렬)
     */
    public List<ChatRoomEntity> findAllByHostSeq(Long hostSeq) {
        return chatRoomJpaRepository.findAllByHostSeq(hostSeq);
    }

    /**
     * 채팅방 고유 식별자로 채팅방을 조회합니다.
     * JOIN FETCH를 사용하여 guestEntity와 hostEntity를 함께 로드합니다.
     *
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @return 채팅방 엔티티 (존재하지 않으면 Optional.empty())
     */
    public Optional<ChatRoomEntity> findByChatRoomSeq(String chatRoomSeq) {
        return Optional.ofNullable(chatRoomJpaRepository.findByChatRoomSeq(chatRoomSeq));
    }

    /**
     * 채팅방 엔티티를 저장합니다.
     *
     * @param chatRoomEntity 저장할 채팅방 엔티티
     * @return 저장된 채팅방 엔티티
     */
    public ChatRoomEntity save(ChatRoomEntity chatRoomEntity) {
        return chatRoomJpaRepository.save(chatRoomEntity);
    }
}
