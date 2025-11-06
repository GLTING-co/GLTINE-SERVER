package glting.server.users.controller.request;

public class UserRequest {
    public record SocialLoginRequest(
            String accessToken
    ) {
    }

    public record NoAccountRequest(
            String type,
            String id,
            String name,
            String birth,
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
            String job,
            String company,
            String school,
            String city,
            String smoking,
            String drinking,
            String religion
    ) {
    }
}
