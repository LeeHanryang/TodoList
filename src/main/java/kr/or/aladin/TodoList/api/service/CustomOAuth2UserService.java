package kr.or.aladin.TodoList.api.service;

import kr.or.aladin.TodoList.enums.RoleEnum;
import kr.or.aladin.TodoList.security.oauth2.OAuth2Util;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final OAuth2Util oAuth2Util;
    private final PasswordEncoder passwordEncoder;

    // 테스트를 위해 분리
    protected OAuth2User delegateLoadUser(OAuth2UserRequest req) {
        return super.loadUser(req);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User oauth2User = delegateLoadUser(req);
        Map<String, Object> attr = oauth2User.getAttributes();

        String provider = req.getClientRegistration().getRegistrationId();
        String providerId = oAuth2Util.extractProviderId(provider, attr);
        String email = oAuth2Util.extractEmail(provider, attr);
        String username = oAuth2Util.extractUserName(provider);

        userService.processOAuth2User(
                username,
                email,
                passwordEncoder.encode(UUID.randomUUID().toString()),
                provider,
                providerId
        );

        return new DefaultOAuth2User(
                Set.of(RoleEnum.USER::getRole),
                attr,
                oAuth2Util.getNameAttributeKey(provider)
        );
    }
}
