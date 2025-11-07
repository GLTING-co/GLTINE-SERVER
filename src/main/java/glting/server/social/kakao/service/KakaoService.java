package glting.server.social.kakao.service;

import glting.server.common.service.CommonService;
import glting.server.social.kakao.domain.KakaoDomain;
import glting.server.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static glting.server.users.controller.response.UserResponse.*;

@Service
@RequiredArgsConstructor
public class KakaoService {
    private final KakaoDomain kakaoDomain;

    private final UserRepository userRepository;

    private final CommonService commonService;

    /**
     * 카카오 로그인 처리 흐름을 수행합니다.
     *
     * @param accessToken 카카오 Access Token
     * @return 로그인된 사용자의 경우 LoginResponse (accessToken, refreshToken),
     *         미가입 사용자의 경우 NoAccountResponse (소셜 타입, 소셜 ID)
     */
    @Transactional
    public Mono<?> loginKakao(String accessToken) {
        return kakaoDomain.getKakaoUser(accessToken)
                .flatMap(kakaoUser ->
                        Mono.justOrEmpty(userRepository.findBySocialId(kakaoUser.id(), "KAKAO"))
                                .map(user -> {
                                    String access = commonService.issueToken(user.getUserSeq(), "ACCESS", "Kakao");
                                    String refresh = commonService.issueToken(user.getUserSeq(), "REFRESH", "Kakao");
                                    commonService.saveToken(user.getUserSeq(), "WHITE", access);

                                    return (Object) new LoginResponse(access, refresh);
                                })
                                .switchIfEmpty(
                                        Mono.fromSupplier(() ->
                                                (Object) new NoAccountResponse("KAKAO", kakaoUser.id())
                                        )
                                )
                );
    }
}
