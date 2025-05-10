package kr.or.aladin.TodoList.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.aladin.TodoList.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * OAuth2 로그인 성공 → JWT 발급 후 프론트엔드로 리다이렉트
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final String redirectUri = "http://localhost:5173/oauth2/callback"; // FE 콜백 URI

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String username = authentication.getName();              // CustomOAuth2UserService가 설정
        String token = authService.generateToken(username);    // JWT 발급

        URI target = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build()
                .toUri();

        response.sendRedirect(target.toString());
    }
}
