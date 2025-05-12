package kr.or.aladin.TodoList.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary   // 동일 타입의 빈이 둘 이상일 때 우선 적용
    public AuthenticationEntryPoint testAuthenticationEntryPoint() {
        return new TestAuthenticationEntryPoint();
    }

    /**
     * 테스트용 EntryPoint – 예외를 그대로 던지지 않고 401 응답만 작성
     */
    static class TestAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request,
                             HttpServletResponse response,
                             AuthenticationException authException) throws IOException {

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "유효하지 않은 토큰(테스트)");
        }
    }
}