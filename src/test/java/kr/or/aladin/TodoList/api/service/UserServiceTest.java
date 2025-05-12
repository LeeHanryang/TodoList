package kr.or.aladin.TodoList.api.service;

import kr.or.aladin.TodoList.api.domain.SocialAccount;
import kr.or.aladin.TodoList.api.domain.User;
import kr.or.aladin.TodoList.api.dto.UserDTO;
import kr.or.aladin.TodoList.api.repository.SocialAccountRepository;
import kr.or.aladin.TodoList.api.repository.UserRepository;
import kr.or.aladin.TodoList.enums.OAuth2Enum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final String username = "oauthUser";
    private final String email = "oauth@test.com";
    private final String encodedPassword = "encoded1234";
    private final String provider = "google";
    private final String providerId = "abc123";
    @Mock
    private UserRepository userRepository;
    @Mock
    private SocialAccountRepository socialAccountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("이미 등록된 소셜 계정이 있는 경우 기존 유저 반환")
    void existingSocialAccount() {
        User user = User.create(username, encodedPassword, email);
        SocialAccount socialAccount = SocialAccount.of(user, OAuth2Enum.GOOGLE, providerId);

        when(socialAccountRepository.findByProviderAndProviderId(OAuth2Enum.GOOGLE, providerId))
                .thenReturn(Optional.of(socialAccount));

        UserDTO result = userService.processOAuth2User(username, email, encodedPassword, provider, providerId);

        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository, never()).save(any());
        verify(socialAccountRepository, never()).save(any());
    }

    @Test
    @DisplayName("기존 이메일 유저가 있고 소셜 계정만 연결하는 경우")
    void existingEmailUser() {
        User user = User.create(username, encodedPassword, email);
        when(socialAccountRepository.findByProviderAndProviderId(OAuth2Enum.GOOGLE, providerId))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDTO result = userService.processOAuth2User(username, email, encodedPassword, provider, providerId);

        assertThat(result.getEmail()).isEqualTo(email);
        verify(socialAccountRepository).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("이메일도 소셜도 모두 없을 경우 새 유저 생성")
    void newUserAndSocialAccount() {
        when(socialAccountRepository.findByProviderAndProviderId(OAuth2Enum.GOOGLE, providerId))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        User savedUser = User.create(username, encodedPassword, email);
        when(userRepository.save(any())).thenReturn(savedUser);

        UserDTO result = userService.processOAuth2User(username, email, encodedPassword, provider, providerId);

        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository).save(any());
        verify(socialAccountRepository).save(any());
    }
}
