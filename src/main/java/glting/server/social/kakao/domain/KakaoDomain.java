package glting.server.social.kakao.domain;

import glting.server.exception.ServerException;
import glting.server.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static glting.server.exception.code.ExceptionCodeMapper.*;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;
import static glting.server.social.kakao.dto.KakaoDto.*;
import static glting.server.social.kakao.util.KakaoUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoDomain {
    @Value("${kakao.oauth.rest-api-key}")
    private String KakaoRestAPIKey;
    @Value("${kakao.oauth.redirect-uri}")
    private String kakaoRedirectURL;

    private final WebClientConfig webClientConfig;


    /**
     * 인가 코드로 카카오 액세스/리프레시 토큰을 발급받습니다.
     *
     * @param code 카카오 인가 코드(authorization_code)
     * @return 카카오 토큰 응답 Mono
     */
    public Mono<KakaoTokenResponse> getKakaoAccessToken(String code) {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", KakaoRestAPIKey);
            form.add("redirect_uri", kakaoRedirectURL);
            form.add("code", code);

            return webClientConfig.createWebClient(KAKAO_AUTH_BASE_URL).post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(KakaoTokenResponse.class)
                    .doOnError(
                            e -> {
                                log.error(e.getMessage());
                            }
                    )
                    .onErrorMap(
                            e -> new ServerException(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    e.getMessage(),
                                    getCode(e.getMessage(), ExceptionType.SERVER)
                            )
                    );
        } catch (Exception e) {
            log.error(e.getMessage());

            return Mono.error(
                    new ServerException(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            getCode(e.getMessage(), ExceptionType.SERVER)
                    )
            );
        }
    }

    /**
     * 액세스 토큰으로 카카오 사용자 정보를 조회합니다.
     *
     * @param accessToken 카카오 액세스 토큰
     * @return 카카오 사용자 응답 Mono
     */

    public Mono<KakaoUserResponse> getKakaoUser(String accessToken) {
        try {
            return webClientConfig.createWebClient(KAKAO_API_BASE_URL).get()
                    .uri("/v2/user/me")
                    .headers(h -> h.setBearerAuth(accessToken))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(KakaoUserResponse.class)
                    .doOnError(
                            e -> {
                                log.error(e.getMessage());
                            }
                    )
                    .onErrorMap(
                            e -> new ServerException(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    e.getMessage(),
                                    getCode(e.getMessage(), ExceptionType.SERVER)
                            )
                    );
        } catch (Exception e) {
            return Mono.error(
                    new ServerException(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            getCode(e.getMessage(), ExceptionType.SERVER)
                    )
            );
        }
    }

    /**
     * 액세스 토큰 방식으로 카카오 로그아웃을 호출합니다.
     * 해당 액세스 토큰이 폐기되며, 동일 토큰을 사용하는 모든 기기에서 로그아웃됩니다.
     *
     * @param accessToken 카카오 액세스 토큰
     * @return 카카오 로그아웃 응답(회원번호 포함) Mono
     */
    public Mono<KakaoUserResponse> logout(String accessToken) {
        try {
            return webClientConfig.createWebClient(KAKAO_API_BASE_URL).post()
                    .uri("/v1/user/logout")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .headers(h -> h.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(KakaoUserResponse.class)
                    .doOnError(
                            e -> log.error(e.getMessage())
                    )
                    .onErrorMap(e ->
                            new ServerException(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    e.getMessage(),
                                    getCode(e.getMessage(), ExceptionType.SERVER)
                            )
                    );
        } catch (Exception e) {
            log.error(e.getMessage());
            return Mono.error(
                    new ServerException(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            getCode(e.getMessage(), ExceptionType.SERVER)
                    )
            );
        }
    }

}