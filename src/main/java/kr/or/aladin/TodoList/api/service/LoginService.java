package kr.or.aladin.TodoList.api.service;

import kr.or.aladin.TodoList.api.domain.User;
import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.api.repository.UserRepository;
import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import kr.or.aladin.TodoList.exception.ApiException;
import kr.or.aladin.TodoList.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtProvider;

    public String authenticate(LoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.USER_NOT_FOUND));
        String role = user.getRoles().iterator().next();

        dto.setUsername(user.getUsername());
        dto.setId(user.getId());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            // 아이디·비밀번호 검증 실패
            throw new ApiException(ErrorCodeEnum.LOGIN_FAILED);
        }

        return jwtProvider.generateToken(dto.getId(), dto.getUsername(), dto.getEmail(), role);
    }
}
