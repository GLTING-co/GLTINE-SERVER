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
}
