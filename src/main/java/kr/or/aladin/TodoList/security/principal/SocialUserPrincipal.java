package kr.or.aladin.TodoList.security.principal;

import kr.or.aladin.TodoList.api.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class SocialUserPrincipal extends DefaultOAuth2User {

    private final UUID internalId;
    private final String username;

    /**
     * @param user        내부 User 엔티티 (DB PK, username, email 등 포함)
     * @param attributes  OAuth2 공급자로부터 받은 attribute map
     * @param authorities 인증된 권한 컬렉션 (예: ROLE_USER)
     */
    public SocialUserPrincipal(User user,
                               Map<String, Object> attributes,
                               Collection<? extends GrantedAuthority> authorities) {
        // DefaultOAuth2User의 생성자에 "email"을 nameAttributeKey로 넘겨주면
        // getName() 호출 시 attributes.get("email")을 반환합니다.
        super(authorities, attributes, "email");
        this.internalId = user.getId();
        this.username = user.getUsername();
    }

    /**
     * 내부 PK 가져오기
     */
    public UUID getInternalId() {
        return internalId;
    }

    /**
     * 서비스에서 쓰이는 username (우리 DB상의 username)
     */
    public String getUsername() {
        return username;
    }
}
