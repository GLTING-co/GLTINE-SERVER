package glting.server.chat.controller.request;

public class ChatRequest {
    public record ChatMessageRequest(
            String chatRoomSeq,
            Long receiverSeq,
            String message,
            Boolean isRead,
            Integer page,
            Integer size
    ) {
    }
}
