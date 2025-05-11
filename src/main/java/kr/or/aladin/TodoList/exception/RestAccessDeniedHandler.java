package kr.or.aladin.TodoList.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {
        // 인가 실패 (권한 부족)
        throw new ApiException(ErrorCodeEnum.ROLES_NOT_MATCH.getStatus(),
                ErrorCodeEnum.ROLES_NOT_MATCH.getMessage());
    }
}