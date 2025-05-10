package kr.or.aladin.TodoList.security.oauth2;

import kr.or.aladin.TodoList.api.dto.UserDTO;
import kr.or.aladin.TodoList.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User oauth2User = super.loadUser(req);
        String provider = req.getClientRegistration().getRegistrationId(); // google, github …

        Map<String, Object> attr = oauth2User.getAttributes();
        String email = switch (provider) {
            case "google" -> (String) attr.get("email");
            case "github" -> (String) attr.get("email");
            case "kakao" -> (String) ((Map<?, ?>) attr.get("kakao_account")).get("email");
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
        String username = provider + "_" + email;   // 소셜 prefix로 중복 회피

        UserDTO user = userService.registerSocialIfAbsent(username, email);

        /* Spring Security 내부 Principal (권한은 ROLE_USER 고정) */
        return new DefaultOAuth2User(
                Set.of(() -> "ROLE_USER"),
                attr,
                "sub"   // unique key attribute name (provider별로 다를 수 있음)
        );
    }
}