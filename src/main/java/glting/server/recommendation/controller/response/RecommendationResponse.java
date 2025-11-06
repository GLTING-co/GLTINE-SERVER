package glting.server.recommendation.controller.response;

public class RecommendationResponse {
    public record UserProfileResponse(
            Long userSeq,
            String name,
            Integer age,
            String gender,
            String sexualType,
            String relationship,
            String image
    ) {
    }
}
