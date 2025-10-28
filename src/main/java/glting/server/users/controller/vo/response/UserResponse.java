package glting.server.users.controller.vo.response;

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
}
