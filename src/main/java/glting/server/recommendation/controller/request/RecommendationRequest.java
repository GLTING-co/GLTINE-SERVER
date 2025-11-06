package glting.server.recommendation.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class RecommendationRequest {
    public record RecommendationFilterRequest(
            @Schema(description = "최소 나이", example = "20") Integer minAge,
            @Schema(description = "최대 나이", example = "30") Integer maxAge,
            @Schema(description = "성적 타입", example = "1") String sexualType,
            @Schema(description = "관계", example = "SINGLE") String relationship,
            @Schema(description = "Member Sequence", example = "1") Long user
    ) {
    }
}
