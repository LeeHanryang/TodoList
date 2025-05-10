package kr.or.aladin.TodoList.api.repository;

import kr.or.aladin.TodoList.api.domain.SocialAccount;
import kr.or.aladin.TodoList.enums.OAuth2Enum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, UUID> {
    Optional<SocialAccount> findByProviderAndProviderId(OAuth2Enum provider, String providerId);
}
