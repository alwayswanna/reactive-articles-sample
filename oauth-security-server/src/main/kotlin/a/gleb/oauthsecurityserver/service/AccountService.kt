package a.gleb.oauthsecurityserver.service

import a.gleb.oauthsecurityserver.db.repository.AccountRepository
import a.gleb.oauthsecurityserver.exception.AccountNotFoundException
import mu.KotlinLogging
import org.springframework.stereotype.Service

var logger = KotlinLogging.logger {  }

@Service
class AccountService(
    var accountRepository: AccountRepository
) {

    /**
     * Method which added additional data to JWT token from account entity.
     * @param username from AuthenticationContext.
     */
    fun enrichJWTByUserInfo(username: String): Map<String, String> {
        var account = accountRepository.findAccountByUsername(username)
            .orElseThrow { AccountNotFoundException("There are no account with username: $username") }
        logger.info { "AccountService, enrichJwt by data: $account" }

        var tokenAdditionalAttributes = HashMap<String, String>()
        tokenAdditionalAttributes["user_id"] = account.id.toString()
        tokenAdditionalAttributes["email"] = account.email
        tokenAdditionalAttributes["username"] = account.username
        return tokenAdditionalAttributes
    }
}