package kr.or.aladin.TodoList.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 사용자 정의 예외 → 상태 코드만 반환 (바디 없음)
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Void> handleApiException(ApiException ex) {
        log.warn("Handled ApiException: {}", ex.getErrorMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .build();
    }

    /**
     * 입력 검증 오류 → 400 Bad Request
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Void> handleValidation(Exception ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .build();
    }

    /**
     * 그 외 모든 예외 → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleAll(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
