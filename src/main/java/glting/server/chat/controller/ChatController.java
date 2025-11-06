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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/list/chat-room")
    @Operation(summary = "회원별 채팅방 전체 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
    })
    public ResponseEntity<BaseResponse<List<GetChatRoomListResponse>>> chatRoomList(HttpServletRequest httpServletRequest) {
        Long hostSeq = (Long) httpServletRequest.getAttribute("userSeq");
        return ResponseEntity.ok()
                .body(BaseResponse.ofSuccess(HttpStatus.OK.value(), chatService.chatRoomList(hostSeq)));
    }

    @GetMapping("/chat-room")
    @Operation(summary = "개인 채팅방 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_002", description = "존재하지 않는 채팅방입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<GetChatRoomResponse>> chatRoom(
            @RequestParam(value = "chatRoomSeq") String chatRoomSeq,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest httpServletRequest
    ) {
        Long hostSeq = (Long) httpServletRequest.getAttribute("userSeq");
        return ResponseEntity.ok()
                .body(BaseResponse.ofSuccess(HttpStatus.OK.value(),
                        chatService.chatRoom(hostSeq, chatRoomSeq, PageRequest.of(page, size))));
    }

    @MessageMapping("/chat/message")
    @Operation(summary = "채팅 메시지 전송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_002", description = "존재하지 않는 채팅방입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public void message(ChatMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Long senderSeq = (Long) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userSeq");

        chatService.sendMessage(senderSeq, request);
    }
}
