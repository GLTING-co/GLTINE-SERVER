package glting.server.recommendation.service;

import glting.server.exception.NotFoundException;
import glting.server.exception.code.ExceptionCodeMapper;
import glting.server.users.entity.UserEntity;
import glting.server.users.entity.UserImageEntity;
import glting.server.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static glting.server.exception.code.ExceptionCodeMapper.getCode;
import static glting.server.recommendation.controller.request.RecommendationRequest.*;
import static glting.server.users.controller.response.UserResponse.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserRepository userRepository;


    /**
     *
     * @param filter
     * @return List<UserProfileResponse>
     */
    public List<UserProfileResponse> getRecommendations(RecommendationFilterRequest filter) {

        // 0. 유효성 검사
        userRepository.findByUserSeq(filter.user())
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionCodeMapper.ExceptionType.NOT_FOUND)
                ));

        // 1. 스와이프한 사용자 제외 추천 목록 조회
        List<UserEntity> recommendedBySwipeUser = userRepository.findRecommendedUsers(filter);

        // 2. 추천인 image 조회
        List<Long> userSeqs = recommendedBySwipeUser.stream()
                .map(UserEntity::getUserSeq)
                .toList();

        // 3. userSeq들에 해당하는 이미지 조회
        List<UserImageEntity> userImages = userRepository.findUserImageByUserSeqs(userSeqs);

        // 4. userSeq 기준으로 imageUrl만 그룹핑
        Map<Long, List<String>> imageMap = userImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getUserEntity().getUserSeq(),
                        Collectors.mapping(UserImageEntity::getImage, Collectors.toList())
                ));

        // 5. response 변환
        return recommendedBySwipeUser.stream()
                .map(user -> new UserProfileResponse(
                        user.getUserSeq(),
                        user.getName(),
                        calculateAge(user.getBirth()),
                        user.getBirth(),
                        user.getGender(),
                        user.getSexualType(),
                        user.getRelationship(),
                        user.getBio(),
                        user.getHeight(),
                        user.getJob(),
                        user.getCompany(),
                        user.getSchool(),
                        user.getCity(),
                        user.getSmoking(),
                        user.getDrinking(),
                        user.getReligion(),
                        user.getOpen(),
                        imageMap.getOrDefault(user.getUserSeq(), List.of()) // 이미지 없으면 빈 리스트
                ))
                .toList();

    }

    private int calculateAge(LocalDate birth) {
        return Period.between(birth, LocalDate.now()).getYears();
    }
}
