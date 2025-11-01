package glting.server.recommendation.controller.vo.request;

public record RecommendationFilterRequest(
        Integer minAge,
        Integer maxAge,
        String sexualType,
        String relationship,
        Long user
) {}