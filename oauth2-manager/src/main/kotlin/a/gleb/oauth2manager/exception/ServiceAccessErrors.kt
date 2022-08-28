package a.gleb.oauth2manager.exception

import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.server.ResponseStatusException

class TokenValidationException(message: String): ResponseStatusException(FORBIDDEN, message)