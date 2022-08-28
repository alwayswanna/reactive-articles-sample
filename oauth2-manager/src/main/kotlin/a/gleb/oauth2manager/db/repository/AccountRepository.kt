package a.gleb.oauth2manager.db.repository

import a.gleb.oauth2manager.db.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository : JpaRepository<Account, UUID> {

    fun findAccountByUsername(username: String): Optional<Account>

    fun findAccountById(id: UUID) : Optional<Account>

    fun deleteAccountById(id: UUID)
}