package a.gleb.oauthservice.db.repository;

import a.gleb.oauthservice.db.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findAccountByUsernameOrEmail(String username, String email);
}
