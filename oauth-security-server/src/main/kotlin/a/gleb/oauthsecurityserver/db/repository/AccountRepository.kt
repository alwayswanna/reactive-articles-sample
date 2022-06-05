package a.gleb.oauthsecurityserver.db.repository

import a.gleb.oauthsecurityserver.db.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository : JpaRepository<Account, UUID> {
    fun findAccountByUsernameOrEmail(username: String, email: String): Optional<Account>
}