package a.gleb.oauthservice.service;

import a.gleb.oauthservice.db.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class OauthUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findAccountByUsernameOrEmail(username, username)
                .map(it -> User.builder()
                        .username(it.getUsername())
                        .password(it.getPassword())
                        .disabled(false)
                        .authorities(new SimpleGrantedAuthority(it.getRole().name()))
                        .build())
                .orElseThrow(() -> {
                    log.warn("{}_error, user with username {}, not found", getClass().getSimpleName(), username);
                    throw new UsernameNotFoundException(String.format("%s_error, user with username %s, NOT_FOUND", getClass().getSimpleName(), username));
                });
    }
}
