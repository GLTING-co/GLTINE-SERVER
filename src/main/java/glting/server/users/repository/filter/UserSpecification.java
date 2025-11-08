package glting.server.users.repository.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import glting.server.users.entity.QUserEntity;
import glting.server.users.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

public class UserSpecification {
    public static BooleanExpression sexualTypeEq(String sexualType, QUserEntity user) {
        return hasText(sexualType) ? user.sexualType.eq(sexualType) : null;
    }

    public static BooleanExpression relationshipEq(String relationship, QUserEntity user) {
        return hasText(relationship) ? user.relationship.eq(relationship) : null;
    }

    public static BooleanExpression genderEq(String gender, QUserEntity user) {
        return hasText(gender) ? user.gender.eq(gender) : null;
    }

    public static BooleanExpression ageBetween(Integer minAge, Integer maxAge, QUserEntity user) {
        if (minAge == null && maxAge == null) return null;

        NumberExpression<Integer> ageExpr = Expressions.numberTemplate(
                Integer.class,
                "floor(datediff(curdate(), {0}) / 365)",
                user.birth
        );

        BooleanExpression minCondition = (minAge != null) ? ageExpr.goe(minAge) : null;
        BooleanExpression maxCondition = (maxAge != null) ? ageExpr.loe(maxAge) : null;

        return allOf(minCondition, maxCondition);
    }

    public static BooleanExpression allOf(BooleanExpression... expressions) {
        return Arrays.stream(expressions)
                .filter(Objects::nonNull)
                .reduce(BooleanExpression::and)
                .orElse(null);
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
