package kr.or.aladin.TodoList.api.contoller;

import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.api.dto.SignUpDTO;
import kr.or.aladin.TodoList.api.dto.TodoDTO;
import kr.or.aladin.TodoList.surpport.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TodoFlowTest extends IntegrationTestSupport {

    private String jwt;
    private UUID todoId;
    private String username;
    private String email;

    @BeforeEach
    void setUp() throws Exception {
        // 1) 매번 유니크한 사용자 생성
        String rand = UUID.randomUUID().toString().substring(0, 8);
        username = "testUser" + rand;
        email = "tester+" + rand + "@test.com";

        SignUpDTO signUp = SignUpDTO.builder()
                .username(username)
                .email(email)
                .password("password")
                .build();
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp)))
                .andExpect(status().isCreated());

        // 2) 로그인하여 JWT 획득
        LoginDTO login = new LoginDTO(null, null, email, "password", null);
        String body = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String rawToken = objectMapper.readTree(body).get("access_token").asText();
        jwt = "Bearer " + rawToken;

        // 3) 기본 Todo 생성
        TodoDTO defaultTodo = TodoDTO.builder()
                .title("기본 Todo")
                .description("기본값")
                .completed(false)
                .build();
        String location = mockMvc.perform(post("/todos")
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultTodo)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        assertThat(location).isNotBlank();
        todoId = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    @DisplayName("Todo 생성")
    void createTodo() throws Exception {
        TodoDTO req = TodoDTO.builder()
                .title("JUnit 공부")
                .description("MockMvc로 통합 테스트 작성")
                .completed(false)
                .build();
        String location = mockMvc.perform(post("/todos")
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
        assertThat(location).isNotBlank();

        UUID createdId = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));
        mockMvc.perform(get("/todos/{id}", createdId)
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("JUnit 공부"));
    }

    @Test
    @DisplayName("Todo 목록 조회")
    void listTodos() throws Exception {
        String body = mockMvc.perform(get("/todos")
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<?> list = objectMapper.readValue(body, List.class);
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("Todo 수정")
    void updateTodo() throws Exception {
        TodoDTO updateReq = TodoDTO.builder()
                .id(todoId)
                .title("JUnit 통합 테스트")
                .description("MockMvc 흐름 테스트")
                .completed(true)
                .build();
        mockMvc.perform(put("/todos/{id}", todoId)
                        .header("Authorization", jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("JUnit 통합 테스트"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("Todo 삭제")
    void deleteTodo() throws Exception {
        mockMvc.perform(delete("/todos/{id}", todoId)
                        .header("Authorization", jwt))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/todos/{id}", todoId)
                        .header("Authorization", jwt))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Todo 검색")
    void searchTodos() throws Exception {
        mockMvc.perform(get("/todos/search")
                        .param("keyword", "기본")
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("기본 Todo"));
    }
}
