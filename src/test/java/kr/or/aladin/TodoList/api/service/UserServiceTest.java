package kr.or.aladin.TodoList.api.service;

import kr.or.aladin.TodoList.api.domain.User;
import kr.or.aladin.TodoList.api.dto.SignUpDTO;
import kr.or.aladin.TodoList.api.dto.UserDTO;
import kr.or.aladin.TodoList.api.repository.UserRepository;
import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import kr.or.aladin.TodoList.enums.RoleEnum;
import kr.or.aladin.TodoList.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String TEST_USER = "testUser";
    private static final String TEST_PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String TEST_EMAIL = "tester@test.com";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("정상적인 회원가입")
    void register_success() {
        SignUpDTO dto = SignUpDTO.builder()
                .username(TEST_USER)
                .password(TEST_PASSWORD)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.existsByUsername(TEST_USER)).thenReturn(false);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        User user = User.create(TEST_USER, ENCODED_PASSWORD, TEST_EMAIL);
        user.addRole(RoleEnum.USER.getRole());

        when(userRepository.save(any())).thenReturn(user);

        UserDTO result = userService.register(dto);

        assertThat(result.getUsername()).isEqualTo(TEST_USER);
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("회원가입 시 사용자명 중복 예외")
    void register_duplicateUsername() {
        SignUpDTO dto = SignUpDTO.builder()
                .username(TEST_USER)
                .password(TEST_PASSWORD)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.existsByUsername(TEST_USER)).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCodeEnum.DUPLICATE_USERNAME.getMessage());
    }

    @Test
    @DisplayName("회원가입 시 이메일 중복 예외")
    void register_duplicateEmail() {
        SignUpDTO dto = SignUpDTO.builder()
                .username(TEST_USER)
                .password(TEST_PASSWORD)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.existsByUsername(TEST_USER)).thenReturn(false);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCodeEnum.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원 조회 성공")
    void getUser_success() {
        User user = User.create(TEST_USER, ENCODED_PASSWORD, TEST_EMAIL);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUser(USER_ID);

        assertThat(result.getUsername()).isEqualTo(TEST_USER);
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("회원 조회 실패")
    void getUser_notFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(USER_ID))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCodeEnum.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 수정 성공 - 사용자명, 이메일, 비밀번호 변경")
    void updateUser_success() {
        User user = User.create(TEST_USER, ENCODED_PASSWORD, TEST_EMAIL);
        UserDTO updateDto = UserDTO.builder()
                .username("newUser")
                .email("new@test.com")
                .password("newPassword")
                .build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any())).thenReturn(user);

        UserDTO result = userService.updateUser(USER_ID, updateDto);

        assertThat(result.getUsername()).isEqualTo("newUser");
        assertThat(result.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    @DisplayName("회원 수정 실패 - 사용자 없음")
    void updateUser_userNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        UserDTO dto = UserDTO.builder().username("updateUser").build();

        assertThatThrownBy(() -> userService.updateUser(USER_ID, dto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCodeEnum.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 수정 실패 - 사용자명 중복")
    void updateUser_duplicateUsername() {
        User user = User.create(TEST_USER, ENCODED_PASSWORD, TEST_EMAIL);
        UserDTO dto = UserDTO.builder().username("duplicateUser").build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("duplicateUser")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(USER_ID, dto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCodeEnum.DUPLICATE_USERNAME.getMessage());
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void deleteUser_success() {
        User user = User.create(TEST_USER, ENCODED_PASSWORD, TEST_EMAIL);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.deleteUser(USER_ID);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("회원 삭제 실패 - 사용자 없음")
    void deleteUser_notFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(USER_ID))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCodeEnum.USER_NOT_FOUND.getMessage());
    }
}
