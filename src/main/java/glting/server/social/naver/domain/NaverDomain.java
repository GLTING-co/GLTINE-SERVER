package glting.server.social.naver.domain;

import glting.server.exception.ServerException;
import glting.server.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static glting.server.exception.code.ExceptionCodeMapper.*;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;
import static glting.server.social.naver.dto.NaverDto.*;
import static glting.server.social.naver.util.NaverUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaverDomain {
    @Value("${naver.oauth.client-id}")
    private String naverClientId;
    @Value("${naver.oauth.client-secret}")
    private String naverClientSecret;
    @Value("${naver.oauth.redirect-uri}")
    private String naverRedirectURL;

    private final WebClientConfig webClientConfig;


    /**
     * 인가 코드로 네이버 액세스/리프레시 토큰을 발급받습니다.
     *
     * @param code 네이버 인가 코드(authorization_code)
     * @return 네이버 토큰 응답 Mono
     */
    public Mono<NaverTokenResponse> getNaverAccessToken(String code) {
        try {
            String apiURL = String.format(
                    "%s/oauth2.0/token?grant_type=authorization_code&client_id=%s&client_secret=%s&redirect_uri=%s&code=%s&state=RANDOM_STATE",
                    NAVER_AUTH_BASE_URL, naverClientId, naverClientSecret, naverRedirectURL, code
            );

            return webClientConfig.createWebClient("").get()
                    .uri(apiURL)
                    .retrieve()
                    .bodyToMono(NaverTokenResponse.class)
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
     * 액세스 토큰으로 네이버 사용자 정보를 조회합니다.
     *
     * @param accessToken 네이버 액세스 토큰
     * @return 네이버 사용자 응답 Mono
     */
    public Mono<NaverUserResponse> getNaverUser(String accessToken) {
        try {
            return webClientConfig.createWebClient(NAVER_API_BASE_URL).get()
                    .uri("/v1/nid/me")
                    .headers(h -> {
                        h.setBearerAuth(accessToken);
                        h.set("X-Naver-Client-Id", naverClientId);
                        h.set("X-Naver-Client-Secret", naverClientSecret);
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(NaverUserResponse.class)
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
     * 액세스 토큰 방식으로 네이버 로그아웃을 호출합니다.
     * 해당 액세스 토큰이 폐기되며, 동일 토큰을 사용하는 모든 기기에서 로그아웃됩니다.
     *
     * @param accessToken 네이버 액세스 토큰
     * @return 네이버 로그아웃 응답 Mono
     */
    public Mono<NaverLogoutResponse> logout(String accessToken) {
        try {
            String apiURL = String.format(
                    "%s/oauth2.0/token?grant_type=delete&client_id=%s&client_secret=%s&access_token=%s&service_provider=NAVER",
                    NAVER_AUTH_BASE_URL, naverClientId, naverClientSecret, accessToken
            );

            return webClientConfig.createWebClient("").get()
                    .uri(apiURL)
                    .retrieve()
                    .bodyToMono(NaverLogoutResponse.class)
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
