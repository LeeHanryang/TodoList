package kr.or.aladin.TodoList.api.domain;

import kr.or.aladin.TodoList.enums.OAuth2Enum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainBuilderTest {

    private static final String TEST_USER = "testUser";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_EMAIL = "tester@test.com";

    @Test
    @DisplayName("User.builder() 커버")
    void userBuilder() {
        User user = User.builder()
                .username(TEST_USER)
                .password(TEST_PASSWORD)
                .email(TEST_EMAIL)
                .build();

        assertThat(user.getUsername()).isEqualTo(TEST_USER);
        assertThat(user.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(user.getId()).isNull();              // id는 @UuidGenerator로 DB 삽입 시 생성
    }

    @Test
    @DisplayName("Todo.builder() 커버")
    void todoBuilder() {
        User author = User.create(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        Todo todo = Todo.builder()
                .user(author)
                .title("T")
                .description("D")
                .completed(true)
                .build();

        assertThat(todo.getUser()).isSameAs(author);
        assertThat(todo.getTitle()).isEqualTo("T");
        assertThat(todo.getDescription()).isEqualTo("D");
        assertThat(todo.isCompleted()).isTrue();
        assertThat(todo.getId()).isNull();
    }

    @Test
    @DisplayName("SocialAccount.builder() 커버")
    void socialAccountBuilder() {
        User user = User.create(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        SocialAccount sa = SocialAccount.builder()
                .user(user)
                .provider(OAuth2Enum.NAVER)
                .providerId("naver-123")
                .build();

        assertThat(sa.getUser()).isSameAs(user);
        assertThat(sa.getProvider()).isEqualTo(OAuth2Enum.NAVER);
        assertThat(sa.getProviderId()).isEqualTo("naver-123");
        assertThat(sa.getId()).isNull();
    }
}
