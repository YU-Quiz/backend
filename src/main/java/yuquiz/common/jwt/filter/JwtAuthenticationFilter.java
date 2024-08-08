package yuquiz.common.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import yuquiz.common.exception.exceptionCode.ExceptionCode;
import yuquiz.common.exception.exceptionCode.JwtExceptionCode;
import yuquiz.common.utils.jwt.JwtProvider;

import java.io.IOException;

import static yuquiz.common.utils.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static yuquiz.common.utils.jwt.JwtProperties.TOKEN_PREFIX;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    private static final String SPECIAL_CHARACTERS_PATTERN = "[`':;|~!@#$%()^&*+=?/{}\\[\\]\\\"\\\\\"]+$";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessTokenGetHeader = request.getHeader(ACCESS_HEADER_VALUE);

        /* 로그인 되어 있지 않은 사용자 */
        if(accessTokenGetHeader == null || !accessTokenGetHeader.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveAccessToken(response, accessTokenGetHeader);

        if (accessToken == null)    // resolveAccessToken 메서드에 의해 accessToken에 문제가 있을 경우.
            return;

        Authentication auth = getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    /* jwt 확인 */
    private String resolveAccessToken(HttpServletResponse response, String accessTokenGetHeader) throws IOException {
        String accessToken = accessTokenGetHeader.substring(TOKEN_PREFIX.length()).trim();

        accessToken = accessToken.replaceAll(SPECIAL_CHARACTERS_PATTERN, "");   // 토큰 끝 특수문자 제거

        try {
            jwtProvider.isExpired(accessToken);      // 만료되었는지
        } catch (ExpiredJwtException e) {
            handleExceptionToken(response, JwtExceptionCode.ACCESS_TOKEN_EXPIRED);
            return null;
        }
        return accessToken;
    }


    /* Authentication 가져오기 */
    private Authentication getAuthentication(String token) {

        String username = jwtProvider.getUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /* 예외 처리 */
    private void handleExceptionToken(HttpServletResponse response, ExceptionCode exceptionCode) throws IOException {

        String messageBody = objectMapper.writeValueAsString(exceptionCode);

        log.error("Error occurred: {}", exceptionCode.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(exceptionCode.getStatus());
        response.getWriter().write(messageBody);
    }
}