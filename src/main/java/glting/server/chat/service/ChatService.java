package glting.server.chat.service;

import glting.server.chat.repository.ChatRoomRepository;
import glting.server.users.entity.UserEntity;
import glting.server.users.entity.UserImageEntity;
import glting.server.users.repository.UserImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static glting.server.chat.controller.response.ChatResponse.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserImageRepository userImageRepository;

    /**
     * 호스트 사용자의 채팅방 목록을 조회합니다.
     *
     * @param hostSeq 호스트 사용자 고유 식별자(PK)
     * @return 채팅방 목록 (게스트 프로필 이미지, 공개 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<GetChatRoomListResponse> chatRoomList(Long hostSeq) {
        hostSeq = 1L;

        return chatRoomRepository.findAllByHostSeq(hostSeq)
                .stream()
                .map(chatRoom -> {
                    UserEntity guestEntity = chatRoom.getGuestEntity();
                    UserImageEntity userImage = userImageRepository.findRepresentImageByUserSeq(guestEntity.getUserSeq());

                    return new GetChatRoomListResponse(chatRoom.getChatRoomSeq(), userImage.getImage(), guestEntity.getOpen());
                })
                .toList();
    }
}
