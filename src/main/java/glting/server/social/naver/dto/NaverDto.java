package glting.server.social.naver.dto;

public class NaverDto {
    public record NaverTokenResponse(
            String access_token,
            String refresh_token,
            String token_type,
            Integer expires_in,
            String error,
            String error_description
    ) {
    }

    public record NaverUserResponse(
            String resultcode,
            String message,
            NaverUserInfo response
    ) {
    }

    public record NaverLogoutResponse(
            String access_token,
            String result
    ) {
    }

    public record NaverUserInfo(
            String id,
            String nickname,
            String name,
            String email,
            String gender,
            String age,
            String birthday,
            String profile_image,
            String birthyear,
            String mobile
    ) {
    }
}
