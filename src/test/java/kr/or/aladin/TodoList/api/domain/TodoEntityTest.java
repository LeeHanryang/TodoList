package kr.or.aladin.TodoList.api.domain;

import kr.or.aladin.TodoList.api.dto.TodoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TodoEntityTest {

    private static final String TEST_USER = "testUser";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_EMAIL = "tester@test.com";

    @Test
    @DisplayName("Todo.create() / update() 동작 검증")
    void create_and_update() {
        // given
        User author = User.create(TEST_USER, TEST_PASSWORD, TEST_EMAIL);

        // when (create)
        Todo todo = Todo.create(author, "제목", "내용", false);

        // then
        assertThat(todo.getUser()).isSameAs(author);
        assertThat(todo.getTitle()).isEqualTo("제목");
        assertThat(todo.getDescription()).isEqualTo("내용");
        assertThat(todo.isCompleted()).isFalse();

        // when (update)
        todo.update("제목2", "내용2", true);

        // then
        assertThat(todo.getTitle()).isEqualTo("제목2");
        assertThat(todo.getDescription()).isEqualTo("내용2");
        assertThat(todo.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("toDto() 변환 검증")
    void toDto_conversion() {
        // given
        User author = User.create(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        Todo todo = Todo.create(author, "T", "C", true);

        // when
        TodoDTO dto = todo.toDto();

        // then
        assertThat(dto.getId()).isEqualTo(todo.getId());
        assertThat(dto.getTitle()).isEqualTo("T");
        assertThat(dto.getDescription()).isEqualTo("C");
        assertThat(dto.isCompleted()).isTrue();
    }
}
