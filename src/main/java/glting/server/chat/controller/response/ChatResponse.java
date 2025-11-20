package glting.server.chat.controller.response;

import java.time.LocalDateTime;
import java.util.List;

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
            List<Message> messages
    ) {
        public record Message(
                String message,
                LocalDateTime chatMessageCreatedAt,
                Boolean isHost
        ) {
        }
    }
}
