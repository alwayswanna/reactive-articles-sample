package a.gleb.oauth2manager.exception

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.server.ResponseStatusException

class AccountExistingException(message: String) : ResponseStatusException(BAD_REQUEST, message)

class DataAccountAccessException(message: String) : ResponseStatusException(BAD_REQUEST, message)

class AccountValidationException(message: String) : ResponseStatusException(BAD_REQUEST, message)