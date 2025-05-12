package kr.or.aladin.TodoList.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class RestAccessDeniedHandlerTest {

    private final RestAccessDeniedHandler handler = new RestAccessDeniedHandler();

    @Test
    @DisplayName("handle() 호출 시 ApiException(ROLES_NOT_MATCH) 발생")
    void handleThrowsApiException() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AccessDeniedException cause = new AccessDeniedException("access denied");

        // when & then
        assertThatThrownBy(() -> handler.handle(request, response, cause))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    // status, errorMessage 검증
                    assertThat(apiEx.getStatus()).isEqualTo(ErrorCodeEnum.ROLES_NOT_MATCH.getStatus());
                    assertThat(apiEx.getErrorMessage()).isEqualTo(ErrorCodeEnum.ROLES_NOT_MATCH.getMessage());
                    // data 필드는 null 이어야 함
                    assertThat(apiEx.getData()).isNull();
                });
    }
}
