package glting.server.chat.repository;

import glting.server.chat.entity.ChatRoomEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
