package glting.server.users.repository;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

import static glting.server.recommendation.controller.request.RecommendationRequest.*;
import static glting.server.recommendation.controller.response.RecommendationResponse.*;

@Mapper
public interface UserMapper {
    List<UserProfileResponse> findRecommendation(
            @Param("filter") RecommendationFilterRequest filter,
            @Param("swipedIds") List<Long> swipedIds
    );
}
