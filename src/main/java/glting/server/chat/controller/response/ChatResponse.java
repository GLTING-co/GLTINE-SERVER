package glting.server.chat.controller.response;

import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public class ChatResponse {
    public record GetChatRoomListResponse(
            String chatRoomSeq,
            String guestProfileImage,
            Boolean open
    ) {
    }

    public record GetChatRoomResponse(
            String chatRoomSeq,
            LocalDateTime chatRoomCreatedAt,
            Long guestSeq,
            String guestName,
            String guestImage,
            Boolean open,
            Page<Message> messages
    ) {
        public record Message(
                String message,
                LocalDateTime chatMessageCreatedAt,
                Boolean isHost
        ) {
        }
    }
}
