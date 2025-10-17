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

    public Optional<UserEntity> findBySocialId(Long socialId, String type) {
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
