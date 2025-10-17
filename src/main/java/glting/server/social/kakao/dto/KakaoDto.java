package glting.server.social.kakao.dto;

public class KakaoDto {
    public record KakaoTokenResponse(
            String access_token,
            Integer expires_in,
            String refresh_token,
            Integer refresh_token_expires_in
    ) {
    }

    public record KakaoUserResponse(
            Long id
    ) {
    }
}
