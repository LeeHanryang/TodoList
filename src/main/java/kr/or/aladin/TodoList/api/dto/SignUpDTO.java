package kr.or.aladin.TodoList.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import kr.or.aladin.TodoList.api.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpDTO {

    @NotBlank(message = "사용자 이름을 입력해주세요.")
    private String username;

    @NotBlank
    private String password;   // 평문 로그인 PW

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    /**
     * 인코딩된 비밀번호를 받아 User 엔티티로 변환
     */
    public User toEntity(String encodedPw) {
        return User.builder()
                .username(username)
                .email(email)
                .password(encodedPw)
                .build();
    }
}