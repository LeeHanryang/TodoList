package kr.or.aladin.TodoList.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.aladin.TodoList.api.service.AuthService;
import kr.or.aladin.TodoList.enums.RoleEnum;
import kr.or.aladin.TodoList.security.principal.SocialUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final String redirectUri = "http://localhost:5173/oauth2/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 1) authentication에서 우리가 만든 Principal 꺼내기
        SocialUserPrincipal principal = (SocialUserPrincipal) authentication.getPrincipal();

        UUID userId = principal.getInternalId();      // DB PK
        String username = principal.getUsername();        // DB username
        String email = principal.getAttribute("email");// OAuth2 공급자에서 받은 이메일
        // 권한은 GrantedAuthority 컬렉션에서 하나만 꺼내든가, 필요에 따라 모두 넣으세요.
        String role = principal.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(RoleEnum.USER.getRole());

        // 2) 토큰 생성
        String token = authService.generateToken(
                userId,
                username,
                email,
                role
        );

        // 3) 프론트로 리다이렉트하면서 token 쿼리 파라미터로 전달
        URI target = UriComponentsBuilder
                .fromUriString(redirectUri)
                .queryParam("token", token)
                .build()
                .toUri();

        response.sendRedirect(target.toString());
    }

}
