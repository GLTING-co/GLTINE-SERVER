package glting.server.chat.service;

import glting.server.chat.entity.ChatMessageEntity;
import glting.server.chat.entity.ChatRoomEntity;
import glting.server.chat.repository.ChatMessageRepository;
import glting.server.chat.repository.ChatRoomRepository;
import glting.server.exception.BadRequestException;
import glting.server.exception.ConflictException;
import glting.server.exception.NotFoundException;
import glting.server.exception.ServerException;
import glting.server.users.entity.UserEntity;
import glting.server.users.entity.UserImageEntity;
import glting.server.users.repository.UserImageRepository;
import glting.server.users.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
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
                    String image = userImageRepository.findRepresentImageByUserSeq(guest.getUserSeq())
                            .map(UserImageEntity::getImage)
                            .orElseThrow(() -> new BadRequestException(
                                    HttpStatus.BAD_REQUEST.value(),
                                    "이미지가 왜 없지? 없으면 안되는데 ~",
                                    getCode("이미지가 왜 없지? 없으면 안되는데 ~", ExceptionType.BAD_REQUEST)
                            ));
                    String recentMessage = chatMessageRepository.findRecentMessageByChatRoomSeq(chatRoom.getChatRoomSeq());

                    return new GetChatRoomListResponse(
                            chatRoom.getChatRoomSeq(), chatRoom.getUpdatedAt(), guest.getUserSeq(),
                            guest.getName(), image, guest.getOpen(), recentMessage, chatRoom.getUnReadNum()
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

    /**
     * 채팅방의 읽지 않은 메시지 수를 초기화합니다.
     *
     * @param userSeq     사용자 고유 식별자(PK)
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     */
    @Transactional
    public void readMessage(Long userSeq, String chatRoomSeq) {
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

        try {
            ChatRoomEntity updateChatRoomEntity = chatRoomEntity.resetUnReadNum();
            chatRoomRepository.save(updateChatRoomEntity);
        } catch (OptimisticLockException e) {
            throw new ConflictException(
                    HttpStatus.CONFLICT.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.CONFLICT)
            );
        } catch (Exception e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.SERVER)
            );
        }
    }

    /**
     * 채팅 메시지를 전송하고 저장합니다.
     * chatRoomMessageSeq가 비어있으면 새 메시지를 전송하고,
     * chatRoomMessageSeq가 있으면 해당 메시지 이전의 메시지들을 읽음 처리합니다.
     *
     * @param senderSeq 발신자 사용자 고유 식별자(PK)
     * @param request   채팅 메시지 요청 (채팅방 식별자, 수신자 식별자, 메시지 내용, 읽음 처리할 메시지 식별자)
     */
    @Transactional
    public void sendMessage(Long senderSeq, ChatMessageRequest request) {
        UserEntity senderEntity = userRepository.findByUserSeq(senderSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        if (request.chatRoomMessageSeq().isEmpty()) {
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

            try {
                if (!request.isRead()) {
                    ChatRoomEntity updateChatRoomEntity = chatRoomEntity.increaseUnReadNum();
                    chatRoomRepository.save(updateChatRoomEntity);
                }
            } catch (OptimisticLockException e) {
                throw new ConflictException(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage(),
                        getCode(e.getMessage(), ExceptionType.CONFLICT)
                );
            } catch (Exception e) {
                throw new ServerException(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getMessage(),
                        getCode(e.getMessage(), ExceptionType.SERVER)
                );
            }

            ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                    .chatRoomEntity(chatRoomEntity)
                    .message(request.message())
                    .isRead(false)
                    .senderEntity(senderEntity)
                    .receiverEntity(receiverEntity)
                    .build();

            chatMessageRepository.save(chatMessage);

            UserEntity guest = chatRoomEntity.getUserA().getUserSeq().equals(request.receiverSeq()) ? chatRoomEntity.getUserB() : chatRoomEntity.getUserA();
            String image = userImageRepository.findRepresentImageByUserSeq(guest.getUserSeq())
                    .map(UserImageEntity::getImage)
                    .orElseThrow(() -> new BadRequestException(
                            HttpStatus.BAD_REQUEST.value(),
                            "이미지가 왜 없지? 없으면 안되는데 ~",
                            getCode("이미지가 왜 없지? 없으면 안되는데 ~", ExceptionType.BAD_REQUEST)
                    ));
            String recentMessage = chatMessageRepository.findRecentMessageByChatRoomSeq(chatRoomEntity.getChatRoomSeq());

            GetRecentChatMessageResponse response = new GetRecentChatMessageResponse(
                    receiverEntity.getUserSeq(), chatRoomEntity.getChatRoomSeq(), chatMessage.getChatMessageSeq(),
                    chatRoomEntity.getUpdatedAt(), guest.getUserSeq(), guest.getName(), image, guest.getOpen(),
                    recentMessage, chatRoomEntity.getUnReadNum()
            );

            messagingTemplate.convertAndSend("/sub/chat/room/" + request.receiverSeq(), response);
        } else {
            ChatMessageEntity chatMessageEntity = chatMessageRepository.findByChatMessageSeq(request.chatRoomMessageSeq())
                    .orElseThrow(() -> new NotFoundException(
                            HttpStatus.NOT_FOUND.value(),
                            "존재하지 않는 채팅 메세지입니다.",
                            getCode("존재하지 않는 채팅 메세지입니다.", ExceptionType.NOT_FOUND)
                    ));

            chatMessageRepository.markMessagesAsReadBefore(request.chatRoomMessageSeq(), chatMessageEntity.getChatMessageSeq());
        }
    }
}
