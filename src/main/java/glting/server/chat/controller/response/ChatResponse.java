package glting.server.chat.controller.response;

public class ChatResponse {
    public record GetChatRoomListResponse(
            String chatRoomSeq,
            String guestProfileImage,
            Boolean open
    ) {
    }
}
