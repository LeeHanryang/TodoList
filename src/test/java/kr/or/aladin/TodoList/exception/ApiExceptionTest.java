package kr.or.aladin.TodoList.exception;

import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionTest {

    @Test
    @DisplayName("ErrorCodeEnum 생성자: status, message, data 확인")
    void constructorWithErrorCodeEnum() {
        // given
        ErrorCodeEnum code = ErrorCodeEnum.MISSING_TOKEN;

        // when
        ApiException ex = new ApiException(code);

        // then
        assertThat(ex.getStatus()).isEqualTo(code.getStatus());
        assertThat(ex.getErrorMessage()).isEqualTo(code.getMessage());
        assertThat(ex.getData()).isNull();
        assertThat(ex).hasMessage(code.getMessage());
    }

    @Test
    @DisplayName("HttpStatus, 메시지 생성자: status 및 message 확인")
    void constructorWithStatusAndMessage() {
        // given
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String customMsg = "잘못된 요청입니다.";

        // when
        ApiException ex = new ApiException(status, customMsg);

        // then
        assertThat(ex.getStatus()).isEqualTo(status);
        assertThat(ex.getErrorMessage()).isEqualTo(customMsg);
        assertThat(ex.getData()).isNull();
        assertThat(ex).hasMessage(customMsg);
    }

    @Test
    @DisplayName("HttpStatus, 메시지, 원인, data 생성자: 모든 필드 확인")
    void constructorWithAllParameters() {
        // given
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = "서버 에러 발생";
        Throwable cause = new IllegalStateException("원인 예외");
        String payload = "추가 데이터";

        // when
        ApiException ex = new ApiException(status, msg, cause, payload);

        // then
        assertThat(ex.getStatus()).isEqualTo(status);
        assertThat(ex.getErrorMessage()).isEqualTo(msg);
        assertThat(ex.getData()).isEqualTo(payload);
        assertThat(ex).hasMessage(msg);
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
