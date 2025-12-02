package glting.server.users.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import glting.server.swipe.entity.QSwipeEntity;
import glting.server.users.entity.QUserEntity;
import glting.server.users.entity.QUserImageEntity;
import glting.server.users.entity.UserEntity;
import glting.server.users.entity.UserImageEntity;
import glting.server.users.filter.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static glting.server.recommendation.controller.request.RecommendationRequest.*;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final UserSpecification userSpecification;

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

    /**
     * 사용자 고유 식별자로 사용자를 조회합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @return 사용자 엔티티 (존재하지 않으면 Optional.empty())
     */
    public Optional<UserEntity> findByUserSeq(Long userSeq) {
        return userJpaRepository.findById(userSeq);
    }

    /**
     * 스와이프한 사용자 제외 후 추천 목록을 조회합니다.
     * 각 검색 조건은 동적으로 처리됩니다.
     *
     * @param filter 추천 필터 조건 (사용자 식별자, 최소/최대 나이, 성향, 관계 상태 등)
     * @param gender 성별 필터 조건
     * @return 추천 사용자 엔티티 목록
     */
    public List<UserEntity> findRecommendedUsers(RecommendationFilterRequest filter, String gender) {
        QUserEntity user = QUserEntity.userEntity;
        QSwipeEntity swipe = QSwipeEntity.swipeEntity;

        return queryFactory
                .selectFrom(user)
                .leftJoin(swipe)
                .on(swipe.toUserSeq.userSeq.eq(user.userSeq)
                        .and(swipe.fromUserSeq.userSeq.eq(filter.user())))
                .where(
                        swipe.toUserSeq.isNull(),
                        user.userSeq.ne(filter.user()),
                        userSpecification.genderEq(gender, user),
                        userSpecification.ageBetween(filter.minAge(), filter.maxAge(), user),
                        userSpecification.sexualTypeEq(filter.sexualType(), user),
                        userSpecification.relationshipEq(filter.relationship(), user)
                )
                .offset((long) filter.size() * filter.page())
                .limit(filter.size())
                .fetch();
    }

    /**
     * 여러 사용자의 이미지 목록을 조회합니다.
     *
     * @param userSeqs 사용자 고유 식별자 목록
     * @return 사용자 이미지 엔티티 목록
     */
    public List<UserImageEntity> findUserImageByUserSeqs(List<Long> userSeqs) {
        return queryFactory
                .selectFrom(QUserImageEntity.userImageEntity)
                .where(QUserImageEntity.userImageEntity.userEntity.userSeq.in(userSeqs))
                .fetch();
    }
}
