package glting.server.users.repository;

import glting.server.recommendation.controller.vo.request.RecommendationFilterRequest;
import glting.server.users.controller.vo.response.UserProfileResponse;
import glting.server.users.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<UserEntity> findAll();

    UserEntity findById(@Param("userSeq") Long userSeq);

    void insertUser(UserEntity user);

    List<UserProfileResponse> findRecommendation(
            @Param("filter") RecommendationFilterRequest filter,
            @Param("swipedIds") List<Long> swipedIds
    );

}
