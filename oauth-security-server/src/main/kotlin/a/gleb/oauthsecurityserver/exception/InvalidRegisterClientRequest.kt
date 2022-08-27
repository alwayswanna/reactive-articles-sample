package a.gleb.oauthsecurityserver.exception

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.server.ResponseStatusException

class InvalidRegisterClientRequest(message: String?) : ResponseStatusException(BAD_REQUEST, message)