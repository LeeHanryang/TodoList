package kr.or.aladin.TodoList.api.domain;

import jakarta.persistence.*;
import kr.or.aladin.TodoList.enums.OAuth2Enum;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "social_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SocialAccount {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OAuth2Enum provider;

    /**
     * 프로바이더가 발급한 고유 ID
     */
    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    /**
     * 연동 대상 User
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
