package glting.server.chat.controller;

import glting.server.base.BaseResponse;
import glting.server.chat.service.ChatService;
import glting.server.exception.handler.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static glting.server.chat.controller.request.ChatRequest.ChatMessageRequest;
import static glting.server.chat.controller.response.ChatResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-service")
@Tag(name = "채팅", description = "담당자(박종훈)")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/DESCRIPTION")
    @Operation(
            operationId = "1",
            summary = "소켓 통신 안내 (필독)",
            description = """
                    <br>
                    
                    <h3>1. 소켓 연결</h3>
                    ws://13.209.162.104:8080/ws-stomp
                    
                    <br>
                    
                    <h3>2. 채팅 전송</h3>
                    엔드포인트: <b>/pub/chat/message</b>
                    <br>※ 반드시 JWT 토큰 포함해야 합니다.
                    <br>※ 읽음처리 소켓통신시 필수값: chatRoomSeq, ChatRoomMessageSeq, isRead
                    ```
                    {
                        "chatRoomSeq": "String",
                        "receiverSeq": "Long",
                        "chatRoomMessageSeq": "String",
                        "message": "String",
                        "isRead": "Boolean"
                    }
                    ```
                    
                    <h3>3. 구독</h3>
                    1. /sub/chat/room/{receiverSeq}
                    ```
                    {
                        "receiverSeq": "Long",
                        "chatRoomSeq": "String",
                        "chatMessageSeq": "String",
                        "chatRoomCreatedAt": "LocalDateTime",
                        "guestSeq": "Long",
                        "guestName": "String",
                        "guestImage": "String",
                        "open": "Boolean",
                        "recentMessage": "String",
                        "unReadNum": "Long"
                    }
                    ```
                    """
    )
    public void description() {
    }

    @GetMapping("/list/chat-room")
    @Operation(summary = "회원별 채팅방 전체 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
    })
    public ResponseEntity<BaseResponse<List<GetChatRoomListResponse>>> chatRoomList(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Long userSeq = (Long) httpServletRequest.getAttribute("userSeq");
        List<GetChatRoomListResponse> response = chatService.chatRoomList(userSeq, PageRequest.of(page, size));

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), response));
    }

    @GetMapping("/chat-room")
    @Operation(summary = "개인 채팅방 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_002", description = "존재하지 않는 채팅방입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<GetChatRoomResponse>> chatRoom(
            @RequestParam(value = "chatRoomSeq") @Schema(description = "채팅방 고유 SEQ - UUID") String chatRoomSeq,
            HttpServletRequest httpServletRequest
    ) {
        Long hostSeq = (Long) httpServletRequest.getAttribute("userSeq");
        GetChatRoomResponse response = chatService.chatRoom(hostSeq, chatRoomSeq);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), response));
    }

    @GetMapping("/message")
    @Operation(summary = "채팅방 메세지 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_002", description = "존재하지 않는 채팅방입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<List<GetMessageResponse>>> getMessage(
            @RequestParam(value = "chatRoomSeq") @Schema(description = "채팅방 고유 SEQ - UUID") String chatRoomSeq,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest httpServletRequest
    ) {
        Long hostSeq = (Long) httpServletRequest.getAttribute("userSeq");
        List<GetMessageResponse> response = chatService.getMessage(hostSeq, chatRoomSeq, PageRequest.of(page, size));

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), response));
    }

    @PutMapping("/message")
    @Operation(
            summary = "채팅 메세지 읽음 확인 API",
            description = """
                    채팅방 들어갔을 때 보내면 됨
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_002", description = "존재하지 않는 채팅방입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<String>> readMessage(
            @RequestParam(value = "chatRoomSeq") @Schema(description = "채팅방 고유 SEQ - UUID") String chatRoomSeq,
            HttpServletRequest httpServletRequest
    ) {
        Long userSeq = (Long) httpServletRequest.getAttribute("userSeq");
        chatService.readMessage(userSeq, chatRoomSeq);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), "SUCCESS"));
    }

    @MessageMapping("/chat/message")
    public void message(ChatMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long senderSeq = (Long) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userSeq");

        chatService.sendMessage(senderSeq, request);
    }
}
