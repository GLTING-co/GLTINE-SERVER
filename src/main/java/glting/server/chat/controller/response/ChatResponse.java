package glting.server.chat.controller.response;

import java.time.LocalDateTime;

public class ChatResponse {
    public record GetChatRoomListResponse(
            String chatRoomSeq,
            LocalDateTime chatRoomCreatedAt,
            Long guestSeq,
            String guestName,
            String guestImage,
            Boolean open,
            String recentMessage,
            Long unReadNum
    ) {
    }

    public record GetChatRoomResponse(
            String chatRoomSeq,
            LocalDateTime chatRoomCreatedAt,
            Long guestSeq,
            String guestName,
            String guestImage,
            Boolean open
    ) {
    }

    public record GetMessageResponse(
            String message,
            LocalDateTime chatMessageCreatedAt,
            Boolean isHost
    ) {
    }
}
