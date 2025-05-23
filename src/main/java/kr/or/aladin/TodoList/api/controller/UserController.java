package kr.or.aladin.TodoList.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.or.aladin.TodoList.api.dto.LoginDTO;
import kr.or.aladin.TodoList.api.dto.SignUpDTO;
import kr.or.aladin.TodoList.api.dto.UserDTO;
import kr.or.aladin.TodoList.api.service.LoginService;
import kr.or.aladin.TodoList.api.service.UserService;
import kr.or.aladin.TodoList.security.principal.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "회원 관리·인증 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoginService loginService;

    /* ───────── 인증 ───────── */

    @Operation(summary = "회원가입", description = "신규 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가입 성공",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 아이디 또는 이메일입니다.", content = @Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@Valid @RequestBody SignUpDTO dto) {
        UserDTO created = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "로그인", description = "JWT 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginDTO.class))),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호가 올바르지 않습니다.", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(
            mediaType = "application/json",
            schemaProperties = {
                    @SchemaProperty(name = "email", schema = @Schema(implementation = String.class)),
                    @SchemaProperty(name = "password", schema = @Schema(implementation = String.class))
            }
    )})
    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@Valid @RequestBody LoginDTO dto) {
        String token = loginService.authenticate(dto);
        return ResponseEntity.ok(new LoginDTO(token));
    }

    /* ───────── 프로필 ───────── */

    @Operation(summary = "계정 조회", description = "회원 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없습니다.", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(userService.getUser(principal.id()));
    }

    @Operation(summary = "계정 수정", description = "회원 정보를 수정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "유효성 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없습니다.", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(
            mediaType = "application/json",
            schemaProperties = {
                    @SchemaProperty(name = "username", schema = @Schema(implementation = String.class)),
                    @SchemaProperty(name = "email", schema = @Schema(implementation = String.class))
            }
    )})
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UserDTO dto
    ) {
        return ResponseEntity.ok(userService.updateUser(principal.id(), dto));
    }

    @Operation(summary = "계정 탈퇴", description = "회원 정보를 삭제 합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없습니다.", content = @Content)
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        userService.deleteUser(principal.id());
        return ResponseEntity.noContent().build();
    }
}
