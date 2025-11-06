package glting.server.config;

import glting.server.common.service.CommonService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

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
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return false;
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = commonService.parseToken(token);
                String type = claims.get("type", String.class);
                Long userSeq = ((Number) claims.get("userSeq")).longValue();

                if (!"ACCESS".equalsIgnoreCase(type)) {
                    response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                    return false;
                }

                if (commonService.isTokenInBlackList(userSeq, token)) {
                    response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                    return false;
                }

                if (commonService.isTokenInWhiteList(userSeq, token)) {
                    response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                    return false;
                }

                attributes.put("accessToken", token);
                attributes.put("type", type);
                attributes.put("userSeq", userSeq);

                return true;

            } catch (Exception e) {
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return false;
            }
        }

        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 후 처리 로직이 필요한 경우 여기에 구현
    }
}

