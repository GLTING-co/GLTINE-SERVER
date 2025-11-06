package glting.server.config;

import glting.server.common.service.CommonService;
import glting.server.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static glting.server.exception.code.ExceptionCodeMapper.*;
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
        String method = request.getMethod();

        if (method.equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        boolean isPermitAllEndpoint = permitAllRequestMatchers.stream()
                .anyMatch(matcher -> matcher.matches(request));

        if (isPermitAllEndpoint) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // JWT 토큰 파싱 및 검증
                Claims claims = commonService.parseToken(token);
                String type = claims.get("type", String.class);
                Long userSeq = ((Number) claims.get("userSeq")).longValue();

                // 토큰 타입이 ACCESS인지 확인
                if (!"ACCESS".equalsIgnoreCase(type)) {
                    throw new UnauthorizedException(
                            HttpStatus.UNAUTHORIZED.value(),
                            "ACCESS 토큰만 사용할 수 있습니다.",
                            getCode("ACCESS 토큰만 사용할 수 있습니다.", ExceptionType.UNAUTHORIZED)
                    );
                }

                // BLACK 리스트에 있는지 확인 (로그아웃된 토큰)
                if (commonService.isTokenInBlackList(userSeq, token)) {
                    throw new UnauthorizedException(
                            HttpStatus.UNAUTHORIZED.value(),
                            "로그아웃된 토큰입니다.",
                            getCode("로그아웃된 토큰입니다.", ExceptionType.UNAUTHORIZED)
                    );
                }

                // WHITE 리스트에 있는지 확인 (유효한 토큰)
                if (!commonService.isTokenInWhiteList(userSeq, token)) {
                    throw new UnauthorizedException(
                            HttpStatus.UNAUTHORIZED.value(),
                            "유효하지 않은 토큰입니다.",
                            getCode("유효하지 않은 토큰입니다.", ExceptionType.UNAUTHORIZED)
                    );
                }

                // 요청 속성에 사용자 정보 저장 (필요시 컨트롤러에서 사용)
                request.setAttribute("accessToken", token);
                request.setAttribute("type", type);
                request.setAttribute("userSeq", userSeq);
                response.setHeader("Authorization", "Bearer " + token);
                filterChain.doFilter(request, response);

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
            } catch (Exception e) {
                throw new UnauthorizedException(
                        HttpStatus.UNAUTHORIZED.value(),
                        e.getMessage(),
                        getCode(e.getMessage(), ExceptionType.UNAUTHORIZED)
                );
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}