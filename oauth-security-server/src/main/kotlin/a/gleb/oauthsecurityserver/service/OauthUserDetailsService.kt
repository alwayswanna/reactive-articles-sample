package a.gleb.oauthsecurityserver.service

import a.gleb.oauthsecurityserver.db.repository.AccountRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class OauthUserDetailsService(
    var accountRepository: AccountRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException("Username is null");
        }
        return accountRepository.findAccountByUsernameOrEmail(username, username)
            .map {
                User.builder()
                    .username(it.username)
                    .password(it.password)
                    .disabled(!it.enabled)
                    .authorities(SimpleGrantedAuthority(it.roles.name))
                    .build()
            }
            .orElseThrow { throw UsernameNotFoundException("User, with username: $username not found") }
    }
}
