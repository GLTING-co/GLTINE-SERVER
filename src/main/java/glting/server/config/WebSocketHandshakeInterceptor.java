package glting.server.config;

import glting.server.common.service.CommonService;
import glting.server.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static glting.server.exception.code.ExceptionCodeMapper.*;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final CommonService commonService;

    /**
     * WebSocket 핸드셰이크 전에 호출됩니다.
     * 토큰을 검증하고 유효하지 않으면 연결을 거부합니다.
     *
     * @param request    HTTP 요청
     * @param response   HTTP 응답
     * @param wsHandler  WebSocket 핸들러
     * @param attributes WebSocket 세션 속성
     * @return true면 연결 허용, false면 연결 거부
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException(
                        HttpStatus.UNAUTHORIZED.value(),
                        "유효하지 않은 토큰입니다.",
                        getCode("유효하지 않은 토큰입니다.", ExceptionType.UNAUTHORIZED)
                );
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = commonService.parseToken(token);
                String type = claims.get("type", String.class);
                Long userSeq = ((Number) claims.get("userSeq")).longValue();

                if (!"ACCESS".equalsIgnoreCase(type)) {
                    throw new UnauthorizedException(
                            HttpStatus.UNAUTHORIZED.value(),
                            "ACCESS 토큰만 사용할 수 있습니다.",
                            getCode("ACCESS 토큰만 사용할 수 있습니다.", ExceptionType.UNAUTHORIZED)
                    );
                }

                if (commonService.isTokenInBlackList(userSeq, token)) {
                    throw new UnauthorizedException(
                            HttpStatus.UNAUTHORIZED.value(),
                            "로그아웃된 토큰입니다.",
                            getCode("로그아웃된 토큰입니다.", ExceptionType.UNAUTHORIZED)
                    );
                }

                if (!commonService.isTokenInWhiteList(userSeq, token)) {
                    throw new UnauthorizedException(
                            HttpStatus.UNAUTHORIZED.value(),
                            "유효하지 않은 토큰입니다.",
                            getCode("유효하지 않은 토큰입니다.", ExceptionType.UNAUTHORIZED)
                    );
                }

                attributes.put("accessToken", token);
                attributes.put("type", type);
                attributes.put("userSeq", userSeq);

                return true;

            } catch (ExpiredJwtException e) {
                throw new UnauthorizedException(
                        HttpStatus.UNAUTHORIZED.value(),
                        "만료된 JWT 입니다.",
                        getCode("만료된 JWT 입니다.", ExceptionType.UNAUTHORIZED)
                );
            } catch (SignatureException e) {
                throw new UnauthorizedException(
                        HttpStatus.UNAUTHORIZED.value(),
                        "잘못된 JWT 입니다.",
                        getCode("잘못된 JWT 입니다.", ExceptionType.UNAUTHORIZED)
                );
            } catch (JwtException e) {
                throw new UnauthorizedException(
                        HttpStatus.UNAUTHORIZED.value(),
                        "JWT 토큰 처리 중 오류가 발생했습니다.",
                        getCode("JWT 토큰 처리 중 오류가 발생했습니다.", ExceptionType.UNAUTHORIZED)
                );
            } catch (UnauthorizedException e) {
                throw e;
            } catch (Exception e) {
                throw new UnauthorizedException(
                        HttpStatus.UNAUTHORIZED.value(),
                        e.getMessage(),
                        getCode(e.getMessage(), ExceptionType.UNAUTHORIZED)
                );
            }
        }

        throw new UnauthorizedException(
                HttpStatus.UNAUTHORIZED.value(),
                "유효하지 않은 토큰입니다.",
                getCode("유효하지 않은 토큰입니다.", ExceptionType.UNAUTHORIZED)
        );
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 후 처리 로직이 필요한 경우 여기에 구현
    }
}

