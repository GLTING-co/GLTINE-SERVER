package glting.server.chat.controller.request;

public class ChatRequest {
    public record ChatSendMessageRequest(
            String message,
            String messageSeq
    ) {
    }

    public record ChatReadMessageRequest(
            String messageSeq
    ) {
    }
}
