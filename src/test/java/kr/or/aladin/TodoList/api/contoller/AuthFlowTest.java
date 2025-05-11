package kr.or.aladin.TodoList.api.contoller;

import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.api.dto.SignUpDTO;
import kr.or.aladin.TodoList.surpport.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 회원가입 → 로그인 → JWT 토큰 발급 검증
 */
class AuthFlowTest extends IntegrationTestSupport {

    @Test
    @DisplayName("회원가입 후 로그인 시 JWT 토큰 발급")
    public void signUp_then_login_and_receive_jwt() throws Exception {
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("tester@test.com"))
                .andExpect(jsonPath("$.username").value("testUser"));

        /* 로그인 */
        LoginDTO login = new LoginDTO(
                null, null, "tester@test.com", "password", null
        );

        String token = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JSON 파싱
        LoginDTO response = objectMapper.readValue(token, LoginDTO.class);

        /* JWT 토큰 유효성 확인 */
        assertThat("Bearer " + response.getToken())
                .isNotBlank()
                .matches("^Bearer\\s+[\\w-]+\\.[\\w-]+\\.[\\w-]+$");

    }
}