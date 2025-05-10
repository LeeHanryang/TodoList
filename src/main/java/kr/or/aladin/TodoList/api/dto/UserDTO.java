package kr.or.aladin.TodoList.api.dto;

import kr.or.aladin.TodoList.api.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Getter
@Builder
public class UserDTO {

    private final UUID id;
    private final String username;
    private final String password;
    private final String email;
    private final Set<String> roles;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /* ───────── Entity → DTO 매핑 ───────── */
    public static UserDTO from(User entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .roles(entity.getRoles())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /* 필요 시 DTO → Entity 변환용 */
    public User toEntity(String encodedPw) {
        return User.builder()
                .id(this.id)
                .username(this.username)
                .password(encodedPw)   // DTO에는 password 미포함
                .email(this.email)
                .roles(this.roles)
                .build();
    }
}
