package kr.or.aladin.TodoList.api.service;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import kr.or.aladin.TodoList.api.domain.SocialAccount;
import kr.or.aladin.TodoList.api.domain.User;
import kr.or.aladin.TodoList.api.dto.SignUpDTO;
import kr.or.aladin.TodoList.api.dto.UserDTO;
import kr.or.aladin.TodoList.api.repository.SocialAccountRepository;
import kr.or.aladin.TodoList.api.repository.UserRepository;
import kr.or.aladin.TodoList.enums.ErrorCodeEnum;
import kr.or.aladin.TodoList.enums.OAuth2Enum;
import kr.or.aladin.TodoList.enums.RoleEnum;
import kr.or.aladin.TodoList.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO register(SignUpDTO dto) {
        // 사용자명 중복 체크
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ApiException(ErrorCodeEnum.DUPLICATE_USERNAME);
        }
        // 이메일 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException(ErrorCodeEnum.DUPLICATE_EMAIL);
        }
        // 비밀번호 인코딩
        String encoded = passwordEncoder.encode(dto.getPassword());

        // User 엔티티 생성
        User user = User.create(dto.getUsername(), encoded, dto.getEmail());
        user.addRole(RoleEnum.USER.getRole());

        return userRepository.save(user).toDto();
    }

    public UserDTO getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.USER_NOT_FOUND)).toDto();
    }

    @Transactional
    public UserDTO updateUser(UUID id, @Valid UserDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.USER_NOT_FOUND));

        /* 닉네임 변경 */
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new ApiException(ErrorCodeEnum.DUPLICATE_USERNAME);
            }
            user.changeUsername(dto.getUsername());
        }

        /* 이메일 변경 */
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ApiException(ErrorCodeEnum.DUPLICATE_EMAIL);
            }
            user.changeEmail(dto.getEmail());
        }

        /* 비밀번호 변경 */
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String encoded = passwordEncoder.encode(dto.getPassword());
            user.changePassword(encoded);
        }

        return userRepository.save(user).toDto();
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCodeEnum.USER_NOT_FOUND));

        userRepository.delete(user);
    }


    public UserDTO registerSocialIfAbsent(String username, String email) {
        return userRepository.findByUsername(username)
                .map(User::toDto)
                .orElseGet(() -> {
                    // 랜덤 비밀번호 생성 후 인코딩 (외부 로그에 노출되지 않음)
                    String randomPassword = UUID.randomUUID().toString();
                    String encoded = passwordEncoder.encode(randomPassword);

                    User user = User.create(username, encoded, email);
                    user.addRole(RoleEnum.USER.getRole());

                    return userRepository.save(user).toDto();
                });
    }

    @Transactional
    public UserDTO processOAuth2User(String username, String email, String encodedPassword,
                                     String provider, String providerId) {
        OAuth2Enum providerEnum = OAuth2Enum.from(provider);
        // 같은 소셜 계정이 등록되어 있는지 확인
        Optional<SocialAccount> existingSA =
                socialAccountRepository.findByProviderAndProviderId(providerEnum, providerId);
        if (existingSA.isPresent()) {
            return UserDTO.from(existingSA.get().getUser());
        }

        // 같은 이메일로 가입된 사용자가 있는지 확인
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 3) 신규 회원 생성
                    User newUser = User.create(username, encodedPassword, email);
                    newUser.addRole(RoleEnum.USER.getRole());
                    return userRepository.save(newUser);
                });

        // SocialAccount 등록
        socialAccountRepository.save(
                SocialAccount.of(user, providerEnum, providerId)
        );

        return UserDTO.from(user);
    }
}
