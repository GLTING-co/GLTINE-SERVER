package glting.server.users.controller.response;

import java.time.LocalDate;
import java.util.List;

public class UserResponse {
    public record LoginResponse(
            String accessToken,
            String refreshToken
    ) {
    }

    public record NoAccountResponse(
            String type,
            String id
    ) {
    }

    public record GetUserResponse(
            String name,
            LocalDate birth,
            String gender,
            String sexualType,
            String relationship,
            String bio,
            Integer height,
            String job,
            String company,
            String school,
            String city,
            String smoking,
            String drinking,
            String religion,
            Boolean open
    ) implements BaseUserInfo {

    }

    public record UserProfileResponse(
            Long userSeq,
            String name,
            int age,
            LocalDate birth,
            String gender,
            String sexualType,
            String relationship,
            String bio,
            Integer height,
            String job,
            String company,
            String school,
            String city,
            String smoking,
            String drinking,
            String religion,
            Boolean open,
            List<String> image
    ) implements BaseUserInfo {

    }

    public record ReIssueTokenResponse(
            String accessToken,
            String refreshToken
    ) {
    }

    public interface BaseUserInfo {
        String name();
        LocalDate birth();
        String gender();
        String sexualType();
        String relationship();
        String bio();
        Integer height();
        String job();
        String company();
        String school();
        String city();
        String smoking();
        String drinking();
        String religion();
        Boolean open();
    }
}
