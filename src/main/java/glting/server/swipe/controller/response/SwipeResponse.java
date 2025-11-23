package glting.server.swipe.controller.response;

public class SwipeResponse {

    public record MatchedResponse(
            boolean matched
    ) {
    }
}