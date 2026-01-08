package glting.server.chat.service;

import glting.server.chat.entity.ChatMessageEntity;
import glting.server.chat.entity.ChatMessageLogEntity;
import glting.server.chat.entity.ChatRoomEntity;
import glting.server.chat.repository.ChatMessageLogRepository;
import glting.server.chat.repository.ChatMessageRepository;
import glting.server.chat.repository.ChatRoomRepository;
import glting.server.exception.BadRequestException;
import glting.server.exception.NotFoundException;
import glting.server.users.entity.UserEntity;
import glting.server.users.entity.UserImageEntity;
import glting.server.users.repository.UserImageRepository;
import glting.server.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static glting.server.chat.controller.request.ChatRequest.*;
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
    private final ChatMessageLogRepository chatMessageLogRepository;
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
        List<ChatRoomEntity> chatRooms = chatRoomPage.getContent();

        if (chatRooms.isEmpty()) {
            return List.of();
        }

        // 배치 조회를 위한 데이터 수집
        List<String> chatRoomSeqs = chatRooms.stream()
                .map(ChatRoomEntity::getChatRoomSeq)
                .toList();

        List<Long> guestSeqs = chatRooms.stream()
                .map(chatRoom -> chatRoom.getUserA().getUserSeq().equals(userSeq)
                        ? chatRoom.getUserB().getUserSeq()
                        : chatRoom.getUserA().getUserSeq())
                .distinct()
                .toList();

        // 배치 조회: 이미지, 최근 메시지, 채팅방 로그
        Map<Long, String> imageMap = userImageRepository.findRepresentImagesByUserSeqs(guestSeqs);
        Map<String, String> recentMessageMap = chatMessageRepository.findRecentMessagesByChatRoomSeqs(chatRoomSeqs);
        Map<String, ChatMessageLogEntity> logMap = chatMessageLogRepository.findByChatRoomEntities(chatRooms);

        // 응답 생성
        return chatRooms.stream()
                .map(chatRoom -> {
                    UserEntity guest = chatRoom.getUserA().getUserSeq().equals(userSeq)
                            ? chatRoom.getUserB()
                            : chatRoom.getUserA();

                    String image = imageMap.get(guest.getUserSeq());
                    if (image == null) {
                        throw new BadRequestException(
                                HttpStatus.BAD_REQUEST.value(),
                                "이미지가 왜 없지? 없으면 안되는데 ~",
                                getCode("이미지가 왜 없지? 없으면 안되는데 ~", ExceptionType.BAD_REQUEST)
                        );
                    }

                    String recentMessage = recentMessageMap.getOrDefault(chatRoom.getChatRoomSeq(), null);

                    // 안 읽은 메시지 개수 계산
                    Long unReadNum = 0L;
                    ChatMessageLogEntity chatMessageLogEntity = logMap.get(chatRoom.getChatRoomSeq());
                    if (chatMessageLogEntity != null) {
                        boolean isUserA = chatRoom.getUserA().getUserSeq().equals(userSeq);
                        ChatMessageEntity lastReadMessage = isUserA
                                ? chatMessageLogEntity.getUserALastReadMessage()
                                : chatMessageLogEntity.getUserBLastReadMessage();

                        String lastReadMessageSeq = lastReadMessage != null ? lastReadMessage.getMessageSeq() : null;
                        unReadNum = chatMessageRepository.countUnreadMessages(
                                chatRoom.getChatRoomSeq(),
                                lastReadMessageSeq,
                                userSeq
                        );
                    } else {
                        // 채팅방 로그가 없으면 모든 메시지를 안 읽은 것으로 간주
                        unReadNum = chatMessageRepository.countUnreadMessages(
                                chatRoom.getChatRoomSeq(),
                                null,
                                userSeq
                        );
                    }

                    return new GetChatRoomListResponse(
                            chatRoom.getChatRoomSeq(), chatRoom.getUpdatedAt(), guest.getUserSeq(),
                            guest.getName(), image, guest.getOpen(), recentMessage, unReadNum
                    );
                })
                .toList();
    }

    /**
     * 호스트 사용자의 특정 채팅방 정보를 조회합니다.
     *
     * @param hostSeq     호스트 사용자 고유 식별자(PK)
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @return 채팅방 정보 (게스트 정보, 프로필 이미지, 공개 여부 포함)
     */
    @Transactional(readOnly = true)
    public GetChatRoomResponse chatRoom(Long hostSeq, String chatRoomSeq) {
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
        return new GetChatRoomResponse(
                chatRoom.getChatRoomSeq(),
                chatRoom.getUpdatedAt(),
                guest.getUserSeq(),
                guest.getName(),
                userImageRepository.findRepresentImageByUserSeq(guest.getUserSeq())
                        .map(UserImageEntity::getImage)
                        .orElseThrow(() -> new BadRequestException(
                                HttpStatus.BAD_REQUEST.value(),
                                "이미지가 왜 없지? 없으면 안되는데 ~",
                                getCode("이미지가 왜 없지? 없으면 안되는데 ~", ExceptionType.BAD_REQUEST)
                        )),
                guest.getOpen()
        );
    }

    /**
     * 채팅방의 메시지 목록을 페이징하여 조회합니다.
     *
     * @param hostSeq     조회하는 사용자 고유 식별자(PK)
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @param pageable    페이징 정보
     * @return 메시지 목록 (메시지 내용, 생성 시간, 호스트 여부)
     */
    @Transactional(readOnly = true)
    public List<GetMessageResponse> getMessage(Long hostSeq, String chatRoomSeq, Pageable pageable) {
        userRepository.findByUserSeq(hostSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        chatRoomRepository.findByChatRoomSeq(chatRoomSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 채팅방입니다.",
                        getCode("존재하지 않는 채팅방입니다.", ExceptionType.NOT_FOUND)
                ));

        Page<ChatMessageEntity> messagePage = chatMessageRepository.findAllByChatRoomSeq(chatRoomSeq, pageable);

        return messagePage.getContent()
                .stream()
                .map(chatMessage -> new GetMessageResponse(
                        chatMessage.getMessage(),
                        chatMessage.getCreatedAt(),
                        chatMessage.getSenderEntity().getUserSeq().equals(hostSeq)
                ))
                .toList();
    }

    @Transactional
    public void sendMessage(Long senderSeq, String chatRoomSeq, ChatSendMessageRequest request) {
        UserEntity senderEntity = userRepository.findByUserSeq(senderSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        ChatRoomEntity chatRoomEntity = chatRoomRepository.findByChatRoomSeq(chatRoomSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 채팅방입니다.",
                        getCode("존재하지 않는 채팅방입니다.", ExceptionType.NOT_FOUND)
                ));

        UserEntity receiverEntity
                = chatRoomEntity.getUserA().getUserSeq().equals(senderSeq) ? chatRoomEntity.getUserB() : chatRoomEntity.getUserA();

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoomEntity(chatRoomEntity)
                .message(request.message())
                .senderEntity(senderEntity)
                .receiverEntity(receiverEntity)
                .build();
        chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/sub/chat/message/" + chatRoomEntity.getChatRoomSeq(), request);

        List<GetChatRoomListResponse> chatRoomListResponse
                = chatRoomList(receiverEntity.getUserSeq(), PageRequest.of(0, Integer.MAX_VALUE));
        messagingTemplate.convertAndSend("/sub/chat/list/" + receiverEntity.getUserSeq(), chatRoomListResponse);
    }

    @Transactional
    public void readMessage(Long userSeq, String chatRoomSeq, ChatReadMessageRequest request) {
        userRepository.findByUserSeq(userSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        ChatRoomEntity chatRoomEntity = chatRoomRepository.findByChatRoomSeq(chatRoomSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 채팅방입니다.",
                        getCode("존재하지 않는 채팅방입니다.", ExceptionType.NOT_FOUND)
                ));

        ChatMessageLogEntity chatMessageLogEntity = chatMessageLogRepository.findByChatRoomEntity(chatRoomEntity)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "채팅방 로그 정보가 존재하지 않습니다.",
                        getCode("채팅방 로그 정보가 존재하지 않습니다.", ExceptionType.NOT_FOUND)
                ));

        // request로 받은 메시지 조회
        ChatMessageEntity readMessageEntity = chatMessageRepository.findByMessageSeq(request.messageSeq())
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 채팅 메세지입니다.",
                        getCode("존재하지 않는 채팅 메세지입니다.", ExceptionType.NOT_FOUND)
                ));

        boolean isUserA = chatRoomEntity.getUserA().getUserSeq().equals(userSeq);

        // 마지막 읽은 메시지 갱신
        chatMessageLogEntity.updateLastReadMessage(isUserA, readMessageEntity);
        ChatMessageLogEntity updatedLogEntity = chatMessageLogRepository.save(chatMessageLogEntity);

        // 상대방의 마지막 읽은 메시지 조회
        ChatMessageEntity opponentLastReadMessage
                = isUserA ? updatedLogEntity.getUserBLastReadMessage() : updatedLogEntity.getUserALastReadMessage();

        // 상대방에게 읽은 메시지 정보 전송
        if (opponentLastReadMessage != null) {
            ChatReadMessageResponse readMessageResponse = new ChatReadMessageResponse(opponentLastReadMessage.getMessageSeq());
            messagingTemplate.convertAndSend("/sub/chat/" + chatRoomSeq + "/message/read", readMessageResponse);
        }
    }
}
