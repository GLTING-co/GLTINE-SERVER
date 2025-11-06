package glting.server.users.controller.response;

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
            String birth,
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
            String religion
    ) {
    }

    public record ReIssueTokenResponse(
            String accessToken,
            String refreshToken
    ) {
    }
}
