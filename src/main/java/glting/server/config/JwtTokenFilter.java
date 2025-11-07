package glting.server.config;

import glting.server.common.service.CommonService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static glting.server.exception.code.ExceptionCodeMapper.ExceptionType;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;

public class JwtTokenFilter extends OncePerRequestFilter {

    private final CommonService commonService;
    private final List<RequestMatcher> permitAllRequestMatchers;

    public JwtTokenFilter(CommonService commonService, List<String> permitAllEndpoints) {
        this.commonService = commonService;
        this.permitAllRequestMatchers = permitAllEndpoints.stream()
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        boolean isPermitAllEndpoint = permitAllRequestMatchers.stream().anyMatch(matcher -> matcher.matches(request));
        if (isPermitAllEndpoint) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization header missing",
                    getCode("Authorization header missing", ExceptionType.UNAUTHORIZED));
            return;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = commonService.parseToken(token);
            String type = claims.get("type", String.class);
            Long userSeq = ((Number) claims.get("userSeq")).longValue();

            if (!"ACCESS".equalsIgnoreCase(type)) {
                writeUnauthorized(response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "ACCESS 토큰만 사용할 수 있습니다.",
                        getCode("ACCESS 토큰만 사용할 수 있습니다.", ExceptionType.UNAUTHORIZED));
                return;
            }

            if (commonService.isTokenInBlackList(userSeq, token)) {
                writeUnauthorized(response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "로그아웃된 토큰입니다.",
                        getCode("로그아웃된 토큰입니다.", ExceptionType.UNAUTHORIZED));
                return;
            }

            if (!commonService.isTokenInWhiteList(userSeq, token)) {
                writeUnauthorized(response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "화이트리스트에 없는 토큰입니다.",
                        getCode("화이트리스트에 없는 토큰입니다.", ExceptionType.UNAUTHORIZED));
                return;
            }

            request.setAttribute("accessToken", token);
            request.setAttribute("type", type);
            request.setAttribute("userSeq", userSeq);
            response.setHeader("Authorization", "Bearer " + token);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            writeUnauthorized(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "만료된 JWT 입니다.",
                    getCode("만료된 JWT 입니다.", ExceptionType.UNAUTHORIZED));
        } catch (SignatureException e) {
            writeUnauthorized(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "잘못된 JWT 입니다.",
                    getCode("잘못된 JWT 입니다.", ExceptionType.UNAUTHORIZED));
        } catch (JwtException e) {
            writeUnauthorized(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "JWT 토큰 처리 중 오류가 발생했습니다.",
                    getCode("JWT 토큰 처리 중 오류가 발생했습니다.", ExceptionType.UNAUTHORIZED));
        } catch (Exception e) {
            writeUnauthorized(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.UNAUTHORIZED));
        }
    }

    private void writeUnauthorized(HttpServletResponse response, int status, String message, String code) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        String body = String.format(
                "{\"httpStatus\": %d, \"message\": \"%s\", \"code\": \"%s\"}",
                status, escapeJson(message), escapeJson(code)
        );
        response.getWriter().write(body);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
