package glting.server.social.naver.service;

import glting.server.common.service.CommonService;
import glting.server.social.naver.domain.NaverDomain;
import glting.server.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static glting.server.users.controller.vo.response.UserResponse.*;

@Service
@RequiredArgsConstructor
public class NaverService {
    private final NaverDomain naverDomain;
    private final UserRepository userRepository;
    private final CommonService commonService;

    /**
     * 네이버 로그인 처리 흐름을 수행합니다.
     *
     * @param accessToken 네이버 인가 코드
     */
    @Transactional
    public Mono<?> loginNaver(String accessToken) {
        return naverDomain.getNaverUser(accessToken)
                .flatMap(naverUser ->
                        Mono.justOrEmpty(userRepository.findBySocialId(naverUser.response().id(), "NAVER"))
                                .map(user -> {
                                    String access = commonService.issueToken(user.getUserSeq(), "ACCESS", "Naver");
                                    String refresh = commonService.issueToken(user.getUserSeq(), "REFRESH", "Naver");
                                    commonService.saveToken(user.getUserSeq(), "WHITE", access);
                                    commonService.saveToken(user.getUserSeq(), "BLACK", refresh);

                                    return (Object) new LoginResponse(access, refresh);
                                })
                                .switchIfEmpty(
                                        Mono.fromSupplier(() ->
                                                (Object) new NoAccountResponse("NAVER", naverUser.response().id())
                                        )
                                )
                );
    }
}
