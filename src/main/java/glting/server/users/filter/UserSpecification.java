package glting.server.users.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import glting.server.users.entity.QUserEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Component
public class UserSpecification {
    public BooleanExpression sexualTypeEq(String sexualType, QUserEntity user) {
        return hasText(sexualType) ? user.sexualType.eq(sexualType) : null;
    }

    public BooleanExpression relationshipEq(String relationship, QUserEntity user) {
        return hasText(relationship) ? user.relationship.eq(relationship) : null;
    }

    public BooleanExpression genderEq(String gender, QUserEntity user) {
        return hasText(gender) ? user.gender.eq(gender) : null;
    }

    public BooleanExpression ageBetween(Integer minAge, Integer maxAge, QUserEntity user) {
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

    public BooleanExpression allOf(BooleanExpression... expressions) {
        return Arrays.stream(expressions)
                .filter(Objects::nonNull)
                .reduce(BooleanExpression::and)
                .orElse(null);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
