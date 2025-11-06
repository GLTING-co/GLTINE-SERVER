package glting.server.recommendation.service;

import glting.server.recommendation.controller.vo.request.RecommendationFilterRequest;
import glting.server.swipe.repository.SwipeRepository;
import glting.server.users.controller.vo.response.UserProfileResponse;
import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserRepository;
import glting.server.users.spec.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserRepository userRepository;
    private final SwipeRepository swipeRepository;

    public List<UserProfileResponse> getRecommendations(RecommendationFilterRequest filter) {

//        UserEntity userEntity = userRepository.findByUserSeq(filter.user());

        // ✅ 1. 내가 스와이프한 사용자 목록 가져오기
        List<Long> swipedIds = swipeRepository.findSwipedUserIds(filter.user());


        return userRepository.findAll(filter,swipedIds);

    }
}
