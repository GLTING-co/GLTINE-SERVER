package glting.server.recommendation.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;

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
