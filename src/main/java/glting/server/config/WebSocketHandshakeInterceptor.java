package glting.server.config;

import glting.server.common.service.CommonService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final CommonService commonService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            deny(response, "Unsupported request");
            return false;
        }

        HttpServletRequest http = servletRequest.getServletRequest();

        String authHeader = http.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            deny(response, "Authorization header missing");
            return false;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = commonService.parseToken(token);
            String type = claims.get("type", String.class);
            Long userSeq = ((Number) claims.get("userSeq")).longValue();

            if (!"ACCESS".equalsIgnoreCase(type)) {
                deny(response, "ACCESS 토큰만 사용할 수 있습니다.");
                return false;
            }

            if (commonService.isTokenInBlackList(userSeq, token)) {
                deny(response, "로그아웃된 토큰입니다.");
                return false;
            }

            if (!commonService.isTokenInWhiteList(userSeq, token)) {
                deny(response, "화이트리스트에 없는 토큰입니다.");
                return false;
            }

            attributes.put("accessToken", token);
            attributes.put("type", type);
            attributes.put("userSeq", userSeq);

            return true;

        } catch (Exception e) {
            deny(response, e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 후처리 필요 시 여기에 구현
    }

    private void deny(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        log.error("WS handshake denied: {}", message);
    }
}