package kr.or.aladin.TodoList.api.service;

import kr.or.aladin.TodoList.api.domain.SocialAccount;
import kr.or.aladin.TodoList.api.domain.User;
import kr.or.aladin.TodoList.api.repository.SocialAccountRepository;
import kr.or.aladin.TodoList.api.repository.UserRepository;
import kr.or.aladin.TodoList.enums.OAuth2Enum;
import kr.or.aladin.TodoList.enums.RoleEnum;
import kr.or.aladin.TodoList.security.principal.SocialUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2Enum provider = OAuth2Enum.from(req.getClientRegistration().getRegistrationId());
        OAuth2User oauth2User = super.loadUser(req);
        Map<String, Object> attr = oauth2User.getAttributes();

        String providerId = extractProviderId(provider, attr);

        // 기존 연동 정보 조회
        SocialAccount sa = socialAccountRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // 신규 연동
                    User user = registerNewSocialUser(provider, providerId, attr);
                    return socialAccountRepository.save(SocialAccount.builder()
                            .provider(provider)
                            .providerId(providerId)
                            .user(user)
                            .build());
                });

        // 최종 user 엔티티
        User user = sa.getUser();
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(RoleEnum.USER.getRole()));

        return new SocialUserPrincipal(user, attr, authorities);
    }

    private User registerNewSocialUser(OAuth2Enum provider,
                                       String providerId,
                                       Map<String, Object> attr) {
        String email = extractEmail(provider, attr);
        String username = provider.name().toLowerCase() + "_" + email;

        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .email(email)
                .roles(Set.of("ROLE_USER"))
                .build();
        return userRepository.save(newUser);
    }

    private String extractProviderId(OAuth2Enum provider, Map<String, Object> attr) {
        return switch (provider) {
            case GOOGLE -> (String) attr.get("sub");
            case KAKAO -> String.valueOf(attr.get("id"));
            case NAVER -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attr.get("response");
                yield (String) response.get("id");
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    private String extractEmail(OAuth2Enum provider, Map<String, Object> attr) {
        return switch (provider) {
            case GOOGLE -> (String) attr.get("email");
            case KAKAO -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) attr.get("kakao_account");
                yield (String) kakaoAccount.get("email");
            }
            case NAVER -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attr.get("response");
                yield (String) response.get("email");
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }
}
