package glting.server.users.controller.request;

import java.time.LocalDate;
import java.util.List;

public class UserRequest {
    public record SocialLoginRequest(
            String accessToken
    ) {
    }

    public record NoAccountRequest(
            String type,
            String id,
            String name,
            LocalDate birth,
            String gender,
            String sexualType,
            String relationship
    ) {
    }

    public record LogoutUserRequest(
            String refreshToken
    ) {
    }

    public record UpdateUserRequest(
            String bio,
            Integer height,
            Integer weight,
            String job,
            String company,
            String school,
            String city,
            String smoking,
            String drinking,
            String religion,
            Boolean open,
            List<String> removeImages
    ) {
    }

    public record ReIssueTokenRequest(
            String refreshToken
    ) {
    }
}
