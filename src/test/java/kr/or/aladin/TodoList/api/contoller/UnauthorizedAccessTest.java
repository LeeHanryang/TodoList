package kr.or.aladin.TodoList.api.contoller;

import kr.or.aladin.TodoList.surpport.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JWT 없이 접근 시 ApiException 검증
 */
class UnauthorizedAccessTest extends IntegrationTestSupport {

    @Test
    @DisplayName("Authorization 헤더 없이 Todo 조회 요청 시 401과 에러 메시지 응답")
    void accessWithoutJwt() throws Exception {
        mockMvc.perform(get("/todos/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("MISSING_TOKEN"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("Authorization 헤더 없이 Todo 목록 조회 시 401과 에러 메시지 응답")
    void listWithoutJwt() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("MISSING_TOKEN"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("Authorization 헤더 없이 Todo 검색 요청 시 401과 에러 메시지 응답")
    void searchWithoutJwt() throws Exception {
        mockMvc.perform(get("/todos/search").param("keyword", "기본"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("MISSING_TOKEN"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }
}
