package a.gleb.oauthsecurityserver.service

import a.gleb.articlecommon.models.rest.ApiResponseModel
import a.gleb.articlecommon.models.rest.RegisterClientRequest
import a.gleb.oauthsecurityserver.configuration.DEFAULT_CLIENT_ID
import a.gleb.oauthsecurityserver.exception.InvalidRegisterClientRequest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class OauthRegisterClientService(
    var registeredClientRepository: RegisteredClientRepository,
    var passwordEncoder: PasswordEncoder
) {


    fun createNewRegisteredClient(registerClientRequest: RegisterClientRequest): ApiResponseModel {
        var registerClientByClientId = registeredClientRepository.findByClientId(registerClientRequest.clientId);
        if (registerClientByClientId != null) {
            throw InvalidRegisterClientRequest("Error, client with clientId ${registerClientRequest.clientId} already exists")
        }

        val client = RegisteredClient.withId(UUID.randomUUID().toString())
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofHours(1))
                    .refreshTokenTimeToLive(Duration.ofDays(90))
                    .build()
            )
            .clientId(DEFAULT_CLIENT_ID)
            .clientSecret(passwordEncoder.encode(registerClientRequest.clientSecret))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri(registerClientRequest.redirectedUri)
            .scope(OidcScopes.OPENID)
            .build()

        try {
            registeredClientRepository.save(client)
        } catch (ex: Exception) {
            throw InvalidRegisterClientRequest(
                "Error, can not create client with clientId ${registerClientRequest.clientId} , message: ${ex.message}")
        }

        return ApiResponseModel.builder()
            .status(HttpStatus.OK)
            .description("Client successfully created")
            .build()
    }

}