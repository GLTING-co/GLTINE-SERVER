package glting.server.chat.service;

import glting.server.chat.entity.ChatMessageEntity;
import glting.server.chat.entity.ChatRoomEntity;
import glting.server.chat.repository.ChatMessageRepository;
import glting.server.chat.repository.ChatRoomRepository;
import glting.server.exception.NotFoundException;
import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserImageRepository;
import glting.server.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static glting.server.chat.controller.request.ChatRequest.ChatMessageRequest;
import static glting.server.chat.controller.response.ChatResponse.*;
import static glting.server.exception.code.ExceptionCodeMapper.*;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * 사용자의 채팅방 목록을 페이징하여 조회합니다.
     *
     * @param userSeq  사용자 고유 식별자(PK)
     * @param pageable 페이징 정보
     * @return 채팅방 목록 (게스트 프로필 이미지, 공개 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<GetChatRoomListResponse> chatRoomList(Long userSeq, Pageable pageable) {
        Page<ChatRoomEntity> chatRoomPage = chatRoomRepository.findAllByUserSeq(userSeq, pageable);
        return chatRoomPage.getContent()
                .stream()
                .map(chatRoom -> {
                    UserEntity guest = chatRoom.getUserA().getUserSeq().equals(userSeq) ? chatRoom.getUserB() : chatRoom.getUserA();
                    String image = userImageRepository.findRepresentImageByUserSeq(guest.getUserSeq()).getImage();

                    return new GetChatRoomListResponse(chatRoom.getChatRoomSeq(), image, guest.getOpen());
                })
                .toList();
    }

    /**
     * 호스트 사용자의 특정 채팅방 정보와 메시지 목록을 조회합니다.
     *
     * @param hostSeq     호스트 사용자 고유 식별자(PK)
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @param pageable    페이징 정보
     * @return 채팅방 정보 및 메시지 목록
     */
    @Transactional(readOnly = true)
    public GetChatRoomResponse chatRoom(Long hostSeq, String chatRoomSeq, Pageable pageable) {
        userRepository.findByUserSeq(hostSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomSeq(chatRoomSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 채팅방입니다.",
                        getCode("존재하지 않는 채팅방입니다.", ExceptionType.NOT_FOUND)
                ));

        UserEntity guest = chatRoom.getUserA().getUserSeq().equals(hostSeq) ? chatRoom.getUserB() : chatRoom.getUserA();
        Page<ChatMessageEntity> messagePage = chatMessageRepository.findAllByChatRoomSeq(chatRoomSeq, pageable);
        return new GetChatRoomResponse(
                chatRoom.getChatRoomSeq(),
                chatRoom.getCreatedAt(),
                guest.getUserSeq(),
                guest.getName(),
                userImageRepository.findRepresentImageByUserSeq(guest.getUserSeq()).getImage(),
                guest.getOpen(),
                messagePage.getContent()
                        .stream()
                        .map(msg -> new GetChatRoomResponse.Message(
                                msg.getMessage(),
                                msg.getCreatedAt(),
                                msg.getSenderEntity().getUserSeq().equals(hostSeq)
                        ))
                        .toList()
        );
    }

    /**
     * 채팅 메시지를 전송하고 저장합니다.
     *
     * @param senderSeq 발신자 사용자 고유 식별자(PK)
     * @param request   채팅 메시지 요청 (채팅방 식별자, 수신자 식별자, 메시지 내용)
     */
    @Transactional
    public void sendMessage(Long senderSeq, ChatMessageRequest request) {
        UserEntity senderEntity = userRepository.findByUserSeq(senderSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        UserEntity receiverEntity = userRepository.findByUserSeq(request.receiverSeq())
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        ChatRoomEntity chatRoomEntity = chatRoomRepository.findByChatRoomSeq(request.chatRoomSeq())
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 채팅방입니다.",
                        getCode("존재하지 않는 채팅방입니다.", ExceptionType.NOT_FOUND)
                ));

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoomEntity(chatRoomEntity)
                .message(request.message())
                .senderEntity(senderEntity)
                .receiverEntity(receiverEntity)
                .build();

        chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/sub/chat/room/" + request.chatRoomSeq(), request);
    }
}
