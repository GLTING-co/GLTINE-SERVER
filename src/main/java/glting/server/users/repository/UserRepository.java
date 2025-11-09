package glting.server.users.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import glting.server.swipe.entity.QSwipeEntity;
import glting.server.users.entity.QUserEntity;
import glting.server.users.entity.QUserImageEntity;
import glting.server.users.entity.UserEntity;
import glting.server.users.entity.UserImageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static glting.server.recommendation.controller.request.RecommendationRequest.*;
import static glting.server.users.repository.filter.UserSpecification.*;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory queryFactory;

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
     *
     * 스와이프한 사용자 제외 후 추천 목를을 조회합니다.
     * 각 검색 조건은 동적으로 처리
     * param : user 사용자 고유 식별자(PK), minAge, maxAge, sexualType, relationship
     * @return 사용자 엔티티 리스트
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
                        user.gender.eq(gender),
                        ageBetween(filter.minAge(), filter.maxAge(), user),
                        sexualTypeEq(filter.sexualType(), user),
                        relationshipEq(filter.relationship(), user)
                )
                .offset((long) filter.size() * filter.page())
                .limit(filter.size())
                .fetch();

    }

    public List<UserImageEntity> findUserImageByUserSeqs(List<Long> userSeqs) {

        return queryFactory
                .selectFrom(QUserImageEntity.userImageEntity)
                .where(QUserImageEntity.userImageEntity.userEntity.userSeq.in(userSeqs))
                .fetch();

    }
}
