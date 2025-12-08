package glting.server.recommendation.controller.request;

public class RecommendationRequest {
    public record RecommendationFilterRequest(
            Integer minAge,
            Integer maxAge,
            String sexualType,
            String relationship,
            Long user,

            int page,

            int size
    ) {
    }
}
