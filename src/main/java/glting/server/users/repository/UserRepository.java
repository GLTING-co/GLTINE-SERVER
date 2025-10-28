package glting.server.users.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import glting.server.users.entity.QUserEntity;
import glting.server.users.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory queryFactory;

    public UserEntity saveUserEntity(UserEntity userEntity) {
        return userJpaRepository.save(userEntity);
    }

    public void flush() {
        userJpaRepository.flush();
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
}
