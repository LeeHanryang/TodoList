package kr.or.aladin.TodoList.api.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.api.dto.SignUpDTO;
import kr.or.aladin.TodoList.surpport.IntegrationTestSupport;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 회원 가입 → 로그인 → 프로필 조회 → 프로필 수정 → 회원 탈퇴 통합 흐름
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserFlowTest extends IntegrationTestSupport {

    private static final String TEST_USER = "testUser";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_EMAIL = "tester@test.com";

    private String jwt;          // Bearer 토큰
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @BeforeEach
    @DisplayName("통합 시나리오 실행을 위한 회원 가입 & 로그인")
    @Transactional
    void setUp() throws Exception {

        SignUpDTO signUp = SignUpDTO.builder()
                .email(TEST_EMAIL)
                .username(TEST_USER)
                .password(TEST_PASSWORD)
                .build();

        mvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(signUp)))
                .andExpect(status().isCreated());

        LoginDTO login = new LoginDTO(null, null, TEST_EMAIL, TEST_PASSWORD, null);

        String body = mvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String rawToken = om.readTree(body).get("access_token").asText();
        jwt = "Bearer " + rawToken;
    }

    @Test
    @Order(1)
    @Transactional
    @DisplayName("계정 조회")
    void getProfile() throws Exception {
        mvc.perform(get("/users/me")
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TEST_USER))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    @Order(2)
    @Transactional
    @DisplayName("계정 수정")
    void updateProfile() throws Exception {
        SignUpDTO update = SignUpDTO.builder()
                .username("updatedUser")
                .email("tester@test.com")
                .password("password")
                .build();

        mvc.perform(put("/users/me")
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"));
    }

    @Test
    @Order(3)
    @Transactional
    @DisplayName("회원 탈퇴")
    void deleteAccount() throws Exception {
        /* 탈퇴 */
        mvc.perform(delete("/users/me")
                        .header("Authorization", jwt))
                .andExpect(status().isNoContent());

        /* 탈퇴 후 동일 토큰으로 접근 시 404(또는 401) 확인 */
        mvc.perform(get("/users/me")
                        .header("Authorization", jwt))
                .andExpect(status().isNotFound());
    }
}