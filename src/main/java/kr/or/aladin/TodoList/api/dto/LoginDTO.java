package kr.or.aladin.TodoList.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LoginDTO {

    @NotNull
    private UUID id;           // 사용자 PK

    @NotBlank
    private String username;

    @NotBlank
    private String password;   // 평문 로그인 PW

    @Email
    private String email;

    private String token;

    public LoginDTO(String token) {
        this.token = token;
    }
 
}