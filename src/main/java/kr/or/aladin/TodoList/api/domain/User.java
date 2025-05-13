package kr.or.aladin.TodoList.api.domain;

import jakarta.persistence.*;
import kr.or.aladin.TodoList.api.dto.UserDTO;
import kr.or.aladin.TodoList.enums.OAuth2Enum;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "user_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class User {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SocialAccount> socialAccounts = new HashSet<>();

    /* 권한 문자열 – 예: ROLE_USER, ROLE_ADMIN */
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false, length = 30)
    private Set<String> roles = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* ====== 편의 메서드 ====== */
    public static User create(String username,
                              String encodedPassword,
                              String email) {

        User user = new User();
        user.username = username;
        user.password = encodedPassword;
        user.email = email;
        return user;
    }

    public void addSocialAccount(OAuth2Enum provider, String providerId) {
        SocialAccount sa = SocialAccount.builder()
                .provider(provider)
                .providerId(providerId)
                .user(this)
                .build();
        socialAccounts.add(sa);
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public void removeRole(String role) {
        this.roles.remove(role);
    }

    /* DTO 변환 메서드 */
    public UserDTO toDto() {
        return UserDTO.from(this);
    }
}
