package kr.or.aladin.TodoList.api.controller;

import jakarta.validation.Valid;
import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.api.dto.SignUpDTO;
import kr.or.aladin.TodoList.api.dto.UserDTO;
import kr.or.aladin.TodoList.api.service.AuthService;
import kr.or.aladin.TodoList.api.service.UserService;
import kr.or.aladin.TodoList.security.principal.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // 인증 관련 엔드포인트
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@Valid @RequestBody SignUpDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@Valid @RequestBody LoginDTO dto) {
        String token = authService.authenticate(dto);
        return ResponseEntity.ok(new LoginDTO(token));
    }

    // 프로필 관련 엔드포인트
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(userService.getUser(principal.id()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UserDTO dto) {
        return ResponseEntity.ok(
                userService.updateUser(principal.id(), dto)
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal CustomUserPrincipal principal) {
        userService.deleteUser(principal.id());
        return ResponseEntity.noContent().build();
    }

}