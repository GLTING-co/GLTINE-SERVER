package glting.server.chat.controller.response;

public class ChatResponse {
    public record GetChatRoomListResponse(
            String guestProfileImage,
            Boolean open
    ) {
    }
}
