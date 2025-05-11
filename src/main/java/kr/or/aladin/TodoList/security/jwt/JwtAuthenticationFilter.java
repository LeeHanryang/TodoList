package kr.or.aladin.TodoList.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtill jwtUtill;    // 주입 필요

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // OAuth2 인가 요청 또는 콜백은 JWT 검사하지 않음
        return path.startsWith("/oauth2/authorize")
                || path.startsWith("/oauth2/redirect")
                || path.startsWith("/login/oauth2");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // "Bearer " 제거
            if (jwtUtill.validate(token)) {
                SecurityContextHolder.getContext()
                        .setAuthentication(jwtUtill.toAuthentication(token));
            }
        }
        chain.doFilter(request, response);
    }
}