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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<List<GetChatRoomListResponse>>> get(HttpServletRequest httpServletRequest) {
        Long hostSeq = (Long) httpServletRequest.getAttribute("userSeq");
        List<GetChatRoomListResponse> response = chatService.chatRoomList(hostSeq);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), response));
    }
}
