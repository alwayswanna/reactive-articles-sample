package a.gleb.oauth2manager.service

import a.gleb.oauth2manager.exception.TokenValidationException
import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.*

val log = KotlinLogging.logger { }

@Service
class SecurityContextHelperService {

    /**
     * Get userId from token.
     */
    fun getIdFromToken(): UUID {
        val principal = SecurityContextHolder.getContext().authentication.principal
        if (principal is Jwt) {
            val accountId = principal.claims["user_id"].toString()
            log.info { "Authorized request: with accountId: $accountId" }
            return UUID.fromString(accountId)
        }

        log.warn { "Access token does not contains claim [user_id], token: $principal" }
        throw TokenValidationException("Invalid access token.")
    }

}