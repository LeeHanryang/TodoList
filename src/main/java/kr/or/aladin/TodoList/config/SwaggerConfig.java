package kr.or.aladin.TodoList.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TodoList API")
                        .version("1.0")
                        .description("할 일 관리 API 문서")
                        .contact(new Contact().name("이준혁").email("ballack02@naver.com")));
    }
}
