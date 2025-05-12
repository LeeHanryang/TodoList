package kr.or.aladin.TodoList.security.oauth2;

import kr.or.aladin.TodoList.api.dto.UserDTO;
import kr.or.aladin.TodoList.api.service.UserService;
import kr.or.aladin.TodoList.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserService userService;
    @Mock
    private OAuth2Util oAuth2Util;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler handler;

    @BeforeEach
    void setUp() {
        // @Value 필드 설정
        ReflectionTestUtils.setField(handler, "frontendUrl", "http://frontend");
    }

    @Test
    @DisplayName("onAuthenticationSuccess: 정상 흐름 — 리다이렉트 URL에 토큰이 추가된다")
    void onAuthenticationSuccess_success() throws Exception {
        // given
        String provider = "kakao";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/login/oauth2/code/" + provider);
        MockHttpServletResponse response = new MockHttpServletResponse();

        Authentication auth = mock(Authentication.class);
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(auth.getPrincipal()).thenReturn(oauth2User);

        Map<String, Object> attrs = Map.of("id", "kk-id");
        when(oauth2User.getAttributes()).thenReturn(attrs);

        when(oAuth2Util.extractProviderId(provider, attrs)).thenReturn("kk-id");
        when(oAuth2Util.extractEmail(provider, attrs)).thenReturn("kakao@test.com");
        when(oAuth2Util.extractUserName(provider)).thenReturn("kakao_user");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPwd");

        UserDTO userDto = UserDTO.builder()
                .id(UUID.randomUUID())
                .username("kakao_user")
                .email("kakao@test.com")
                .roles(Set.of("ROLE_USER"))
                .build();

        when(userService.processOAuth2User(
                "kakao_user", "kakao@test.com", "encodedPwd", provider, "kk-id"
        )).thenReturn(userDto);

        when(jwtUtil.generateToken(
                userDto.getId(),
                userDto.getUsername(),
                userDto.getEmail(),
                "ROLE_USER"
        )).thenReturn("jwt-token");

        // when
        handler.onAuthenticationSuccess(request, response, auth);

        String redirectUrl = response.getRedirectedUrl();
        // then
        assertThat(redirectUrl)
                .startsWith("http://frontend/login/oauth2/code/" + provider)
                .contains("token=jwt-token");

        verify(oAuth2Util).extractProviderId(provider, attrs);
        verify(oAuth2Util).extractEmail(provider, attrs);
        verify(oAuth2Util).extractUserName(provider);
        verify(userService).processOAuth2User(
                "kakao_user", "kakao@test.com", "encodedPwd", provider, "kk-id"
        );
        verify(jwtUtil).generateToken(
                userDto.getId(), userDto.getUsername(), userDto.getEmail(), "ROLE_USER"
        );
    }
}
