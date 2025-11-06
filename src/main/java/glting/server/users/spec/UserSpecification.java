package glting.server.users.spec;

import glting.server.users.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecification {
    public static Specification<UserEntity> withGender(String gender) {
        return (root, query, cb) ->
                gender == null ? null : cb.notEqual(root.get("gender"), gender);
    }

    public static Specification<UserEntity> withSexualType(String sexualType) {
        return (root, query, cb) ->
                sexualType == null ? null : cb.equal(root.get("sexualType"), sexualType);
    }

    public static Specification<UserEntity> withRelationship(String relationship) {
        return (root, query, cb) ->
                relationship == null ? null : cb.equal(root.get("relationship"), relationship);
    }

    public static Specification<UserEntity> withAgeBetween(Integer minAge, Integer maxAge) {
        return (root, query, cb) -> {
            if (minAge == null && maxAge == null) return null;

            LocalDate now = LocalDate.now();
            LocalDate minBirth = (maxAge != null) ? now.minusYears(maxAge) : LocalDate.of(1900, 1, 1);
            LocalDate maxBirth = (minAge != null) ? now.minusYears(minAge) : now;

            return cb.between(root.get("birth"), minBirth.toString(), maxBirth.toString());
        };
    }
}
