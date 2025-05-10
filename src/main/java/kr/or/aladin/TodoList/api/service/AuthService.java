package kr.or.aladin.TodoList.api.service;

import jakarta.validation.Valid;
import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.security.jwt.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtill jwtProvider;

    public String authenticate(@Valid LoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );
        return jwtProvider.generateToken(dto.id(), dto.username(), dto.email(), dto.token());
    }

    public String generateToken(String username) {
    }
}
