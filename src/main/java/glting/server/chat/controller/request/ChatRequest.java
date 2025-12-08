package glting.server.chat.controller.request;

public class ChatRequest {
    public record ChatMessageRequest(
            String chatRoomSeq,
            String chatRoomMessageSeq,
            Long receiverSeq,
            String message,
            Boolean isRead
    ) {
    }
}
