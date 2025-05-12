package kr.or.aladin.TodoList.api.domain;

import kr.or.aladin.TodoList.api.dto.UserDTO;
import kr.or.aladin.TodoList.enums.OAuth2Enum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class UserEntityTest {

    private static final String TEST_USER = "testUser";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_EMAIL = "tester@test.com";

    @Test
    @DisplayName("User.create() 기본 필드 검증")
    void create_defaults() {
        // when
        User user = User.create(TEST_USER, TEST_PASSWORD, TEST_EMAIL);

        // then
        assertThat(user.getUsername()).isEqualTo(TEST_USER);
        assertThat(user.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(user.getSocialAccounts()).isEmpty();
        assertThat(user.getRoles()).isEmpty();
    }

    @Test
    @DisplayName("소셜 계정·권한 추가/제거 편의 메서드")
    void socialAndRole_management() {
        // given
        User user = User.create(TEST_USER, TEST_PASSWORD, TEST_EMAIL);

        // when
        user.addSocialAccount(OAuth2Enum.NAVER, "naver-999");

        // then
        assertThat(user.getSocialAccounts())
                .extracting("provider", "providerId")
                .containsExactly(tuple(OAuth2Enum.NAVER, "naver-999"));

        // 역할 추가/제거 검증
        user.addRole("ROLE_USER");
        assertThat(user.getRoles()).contains("ROLE_USER");

        user.removeRole("ROLE_USER");
        assertThat(user.getRoles()).doesNotContain("ROLE_USER");
    }


    @Test
    @DisplayName("필드 변경 메서드 검증")
    void field_change_methods() {
        // given
        User user = User.create(TEST_USER, TEST_PASSWORD, TEST_EMAIL);

        // when
        user.changeUsername("newName");
        user.changePassword("newPass");
        user.changeEmail("new@test.com");

        // then
        assertThat(user.getUsername()).isEqualTo("newName");
        assertThat(user.getPassword()).isEqualTo("newPass");
        assertThat(user.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    @DisplayName("toDto() 변환 검증")
    void toDto_conversion() {
        // given
        User user = User.create(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        user.addRole("ROLE_ADMIN");

        // when
        UserDTO dto = user.toDto();

        // then
        assertThat(dto.getUsername()).isEqualTo(TEST_USER);
        assertThat(dto.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(dto.getRoles()).contains("ROLE_ADMIN");
    }
}
