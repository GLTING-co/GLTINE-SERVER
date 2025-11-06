package glting.server.users.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import glting.server.exception.NotFoundException;
import glting.server.exception.code.ExceptionCodeMapper;
import glting.server.recommendation.controller.vo.request.RecommendationFilterRequest;
import glting.server.users.controller.vo.response.UserProfileResponse;
import glting.server.users.entity.QUserEntity;
import glting.server.users.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static glting.server.exception.code.ExceptionCodeMapper.*;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final UserMapper userMapper;

    /**
     * 사용자 엔티티를 저장합니다.
     *
     * @param userEntity 저장할 사용자 엔티티
     * @return 저장된 사용자 엔티티
     */
    public UserEntity saveUserEntity(UserEntity userEntity) {
        return userJpaRepository.save(userEntity);
    }

    /**
     * 소셜 ID와 소셜 타입(KAKAO, NAVER, GOOGLE)에 따라 사용자를 조회합니다.
     *
     * @param socialId 소셜 플랫폼 사용자 ID
     * @param type     소셜 플랫폼 종류 (KAKAO, NAVER, GOOGLE)
     * @return 해당 소셜 계정에 연결된 사용자 엔티티
     */
    public Optional<UserEntity> findBySocialId(String socialId, String type) {
        QUserEntity userEntity = QUserEntity.userEntity;

        BooleanExpression condition = switch (type.toUpperCase()) {
            case "KAKAO" -> userEntity.kakaoId.eq(socialId);
            case "NAVER" -> userEntity.naverId.eq(socialId);
            case "GOOGLE" -> userEntity.googleId.eq(socialId);
            default -> null;
        };

        if (condition == null) {
            return Optional.empty();
        }

        UserEntity result = queryFactory
                .selectFrom(userEntity)
                .where(condition)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    public List<UserEntity> findAll(Specification<UserEntity> specification) {



        return userJpaRepository.findAll(specification);
    }


    public List<UserProfileResponse> findAll(RecommendationFilterRequest request, List<Long> swipedIds) {

        return userMapper.findRecommendation(request, swipedIds);
    }

    public List<UserEntity> testMyBatis() {

        return userMapper.findAll();
    }

    public void saveAll(List<UserEntity> users) {
        userJpaRepository.saveAll(users);
    }

    public long count(){
        return userJpaRepository.count();
    }


    /**
     * 사용자 고유 식별자로 사용자를 조회합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @return 사용자 엔티티 (존재하지 않으면 Optional.empty())
     */
    public Optional<UserEntity> findByUserSeq(Long userSeq) {
        return userJpaRepository.findById(userSeq);
    }
}
