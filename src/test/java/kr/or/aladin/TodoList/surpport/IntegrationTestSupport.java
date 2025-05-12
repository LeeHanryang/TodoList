package kr.or.aladin.TodoList.surpport;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

/**
 * 통합 테스트용 공통 부모 클래스
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    static void sqliteMemoryDb(DynamicPropertyRegistry registry) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String dbName = "testdb_" + uuid;
        registry.add("spring.datasource.url",
                () -> "jdbc:sqlite:file:" + dbName + "?mode=memory&cache=shared");
        registry.add("spring.datasource.driver-class-name",
                () -> "org.sqlite.JDBC");
    }

    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}

