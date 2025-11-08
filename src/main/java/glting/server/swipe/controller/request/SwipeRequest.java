package glting.server.swipe.controller.request;

public class SwipeRequest {

    public record DislikeRequest(
            Long toUserSeq
    ) {
    }
}
