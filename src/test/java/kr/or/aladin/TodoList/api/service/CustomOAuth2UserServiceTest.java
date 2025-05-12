package kr.or.aladin.TodoList.api.service;

import kr.or.aladin.TodoList.enums.RoleEnum;
import kr.or.aladin.TodoList.security.oauth2.OAuth2Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

    @InjectMocks
    CustomOAuth2UserService target;

    @Mock
    UserService userService;

    @Mock
    OAuth2Util oAuth2Util;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    OAuth2UserRequest userRequest;

    @Mock
    OAuth2User mockOAuth2User;

    @Mock
    ClientRegistration clientRegistration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("loadUser()는 OAuth2User를 반환하고 userService를 호출한다")
    void loadUser_success() {
        // given
        String provider = "google";
        String providerId = "123456";
        String email = "test@example.com";
        String username = "google_user";
        String encodedPassword = "encoded-password";

        Map<String, Object> attributes = Map.of("sub", providerId, "email", email);

        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn(provider);
        when(oAuth2Util.extractProviderId(provider, attributes)).thenReturn(providerId);
        when(oAuth2Util.extractEmail(provider, attributes)).thenReturn(email);
        when(oAuth2Util.extractUserName(provider)).thenReturn(username);
        when(oAuth2Util.getNameAttributeKey(provider)).thenReturn("sub");
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);
        when(mockOAuth2User.getAttributes()).thenReturn(attributes);

        CustomOAuth2UserService spyService = spy(target);
        doReturn(mockOAuth2User).when(spyService).delegateLoadUser(userRequest);

        // when
        OAuth2User result = spyService.loadUser(userRequest);

        // then
        verify(userService).processOAuth2User(username, email, encodedPassword, provider, providerId);
        assertThat(result.getAttributes()).isEqualTo(attributes);
        assertThat(result.getAuthorities()).anyMatch(auth -> auth.getAuthority().equals(RoleEnum.USER.getRole()));
    }
}
