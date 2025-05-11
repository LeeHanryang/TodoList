package kr.or.aladin.TodoList.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        ErrorCodeEnum errorCode = (header == null || !header.startsWith("Bearer "))
                ? ErrorCodeEnum.MISSING_TOKEN
                : ErrorCodeEnum.INVALID_TOKEN;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", errorCode.name());
        errorResponse.put("message", errorCode.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
