package kr.or.aladin.TodoList.api.service;

import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.security.jwt.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtill jwtProvider;

    public String authenticate(LoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        return jwtProvider.generateToken(dto.getId(), dto.getUsername(), dto.getEmail(), dto.getToken());
    }

    public String generateToken(UUID id, String username, String email, String role) {
        return jwtProvider.generateToken(id, username, email, role);
    }
}
