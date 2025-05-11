package kr.or.aladin.TodoList.api.contoller;

import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.api.dto.SignUpDTO;
import kr.or.aladin.TodoList.surpport.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 존재하지 않는 리소스 접근시 404 반환 검증
 */
class InvalidIdTest extends IntegrationTestSupport {

    private String jwt;

    @BeforeEach
    void initUser() throws Exception {
        /* 회원가입 */
        String testUser = "testUser";
        String testPassword = "password";
        String testEmail = "tester@test.com";

        SignUpDTO signUp = SignUpDTO.builder()
                .email(testEmail)
                .username(testUser)
                .password(testPassword)
                .build();

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(signUp)))
                .andExpect(status().isCreated());

        /* 로그인 */
        LoginDTO login = new LoginDTO(null, null, "tester@test.com", "password", null);

        String token = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String extractedToken = objectMapper.readTree(token).get("access_token").asText();

        jwt = "Bearer " + extractedToken; // JWT 저장
    }

    @Test
    @DisplayName("잘못된 Todo ID 요청 시 404 반환")
    void wrongTodoId() throws Exception {
        mockMvc.perform(get("/todos/{id}", UUID.randomUUID())
                        .header("Authorization", jwt))
                .andExpect(status().isNotFound());
    }
}