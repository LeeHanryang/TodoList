package kr.or.aladin.TodoList.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // JWT 토큰 검증 로직 추가
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            // 토큰 검증 후 SecurityContext 설정 (추가 구현 필요)
        }

        chain.doFilter(request, response);
    }
}
