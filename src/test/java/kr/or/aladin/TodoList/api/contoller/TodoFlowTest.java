package kr.or.aladin.TodoList.api.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.api.dto.SignUpDTO;
import kr.or.aladin.TodoList.api.dto.TodoDTO;
import kr.or.aladin.TodoList.surpport.IntegrationTestSupport;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Todo 생성 → 목록 → 수정 → 삭제 통합 흐름
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoFlowTest extends IntegrationTestSupport {

    private String jwt;          // Bearer 토큰
    private UUID todoId;         // 생성된 Todo 식별자

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @BeforeAll
    @DisplayName("Todo 생성 전 로그인 세팅")
    void setUp() throws Exception {
        String testUser = "testUser";
        String testPassword = "password";
        String testEmail = "tester@test.com";

        SignUpDTO signUp = SignUpDTO.builder()
                .email(testEmail)
                .username(testUser)
                .password(testPassword)
                .build();

        mvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(signUp)))
                .andExpect(status().isCreated());

        LoginDTO login = new LoginDTO(null, null, testEmail, testPassword, null);

        String body = mvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String rawToken = om.readTree(body).get("token").asText();
        jwt = "Bearer " + rawToken;

        TodoDTO req = TodoDTO.builder()
                .title("기본 Todo")
                .description("기본값")
                .completed(false)
                .build();

        String location = mvc.perform(post("/todos")
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        assertThat(location).isNotBlank();
        todoId = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    @Order(1)
    @DisplayName("Todo 생성")
    void createTodo() throws Exception {
        TodoDTO req = TodoDTO.builder()
                .title("JUnit 공부")
                .description("MockMvc로 통합 테스트 작성")
                .completed(false)
                .build();

        String location = mvc.perform(post("/todos")
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        assertThat(location).isNotBlank();

        UUID createdId = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));

        mvc.perform(get("/todos/{id}", createdId)
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("JUnit 공부"));
    }

    @Test
    @Order(2)
    @DisplayName("Todo 목록 조회")
    void listTodos() throws Exception {
        String body = mvc.perform(get("/todos")
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<?> list = om.readValue(body, List.class);
        assertThat(list).isNotEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("Todo 수정")
    void updateTodo() throws Exception {
        TodoDTO updateReq = TodoDTO.builder()
                .id(todoId)
                .title("JUnit 통합 테스트")
                .description("MockMvc 흐름 테스트")
                .completed(true)
                .build();

        mvc.perform(put("/todos/{id}", todoId)
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("JUnit 통합 테스트"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @Order(4)
    @DisplayName("Todo 삭제")
    void deleteTodo() throws Exception {
        mvc.perform(delete("/todos/{id}", todoId)
                        .header("Authorization", jwt))
                .andExpect(status().isNoContent());

        mvc.perform(get("/todos/{id}", todoId)
                        .header("Authorization", jwt))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @DisplayName("Todo 검색")
    void searchTodos() throws Exception {
        mvc.perform(get("/todos/search")
                        .param("keyword", "기본")
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("기본 Todo"));
    }
}
