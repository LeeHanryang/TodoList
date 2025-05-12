package kr.or.aladin.TodoList.security.oauth2;

import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import kr.or.aladin.TodoList.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OAuth2UtilTest {

    private final OAuth2Util util = new OAuth2Util();

    @Test
    @DisplayName("generateRandomString(): 길이 8, 16진수 패턴")
    void generateRandomString_lengthAndPattern() {
        String s = util.generateRandomString();
        assertThat(s)
                .isNotNull()
                .hasSize(8)
                .matches("[0-9a-f]{8}");
    }

    @Test
    @DisplayName("extractUserName(): provider_prefix + '_' + 랜덤 8자리")
    void extractUserName_pattern() {
        String name = util.extractUserName("google");
        assertThat(name)
                .matches("google_[0-9a-f]{8}");
    }

    @Test
    @DisplayName("extractEmail(): GOOGLE")
    void extractEmail_google() {
        Map<String, Object> attrs = Map.of("email", "g@test.com");
        assertThat(util.extractEmail("google", attrs))
                .isEqualTo("g@test.com");
    }

    @Test
    @DisplayName("extractEmail(): KAKAO")
    void extractEmail_kakao() {
        Map<String, Object> kakaoAccount = Map.of(
                "email", "k@test.com",
                "profile", Map.of("nickname", "카카오")
        );
        Map<String, Object> attrs = Map.of("kakao_account", kakaoAccount);
        assertThat(util.extractEmail("kakao", attrs))
                .isEqualTo("k@test.com");
    }

    @Test
    @DisplayName("extractEmail(): NAVER")
    void extractEmail_naver() {
        Map<String, Object> response = Map.of(
                "email", "n@test.com",
                "name", "네이버"
        );
        Map<String, Object> attrs = Map.of("response", response);
        assertThat(util.extractEmail("naver", attrs))
                .isEqualTo("n@test.com");
    }

    @Test
    @DisplayName("extractProviderId(): GOOGLE")
    void extractProviderId_google() {
        Map<String, Object> attrs = Map.of("sub", "g-id");
        assertThat(util.extractProviderId("google", attrs))
                .isEqualTo("g-id");
    }

    @Test
    @DisplayName("extractProviderId(): KAKAO")
    void extractProviderId_kakao() {
        Map<String, Object> attrs = Map.of("id", 12345);
        assertThat(util.extractProviderId("kakao", attrs))
                .isEqualTo("12345");
    }

    @Test
    @DisplayName("extractProviderId(): NAVER")
    void extractProviderId_naver() {
        Map<String, Object> resp = Map.of("id", "naver-id");
        Map<String, Object> attrs = Map.of("response", resp);
        assertThat(util.extractProviderId("naver", attrs))
                .isEqualTo("naver-id");
    }

    @Test
    @DisplayName("extractEmail(): 지원하지 않는 provider → ApiException")
    void extractEmail_unsupported() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> util.extractEmail("foo", Map.of())
        );
        assertThat(ex.getStatus())
                .isEqualTo(ErrorCodeEnum.UNSUPPORTED_PROVIDER.getStatus());
        assertThat(ex.getErrorMessage())
                .isEqualTo(ErrorCodeEnum.UNSUPPORTED_PROVIDER.getMessage());
    }

    @Test
    @DisplayName("extractProviderId(): 지원 안 함 → ApiException")
    void extractProviderId_unsupported() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> util.extractProviderId("foo", Map.of())
        );
        assertThat(ex.getStatus())
                .isEqualTo(ErrorCodeEnum.UNSUPPORTED_PROVIDER.getStatus());
        assertThat(ex.getErrorMessage())
                .isEqualTo(ErrorCodeEnum.UNSUPPORTED_PROVIDER.getMessage());
    }

    @Test
    @DisplayName("getNameAttributeKey(): 지원 안 함 → ApiException")
    void getNameAttributeKey_unsupported() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> util.getNameAttributeKey("foo")
        );
        assertThat(ex.getStatus())
                .isEqualTo(ErrorCodeEnum.UNSUPPORTED_PROVIDER.getStatus());
        assertThat(ex.getErrorMessage())
                .isEqualTo(ErrorCodeEnum.UNSUPPORTED_PROVIDER.getMessage());
    }
}
