package a.gleb.oauthservice.service;

import a.gleb.articlecommon.models.rest.ApiResponseModel;
import a.gleb.articlecommon.models.rest.RegisterClientRequest;
import a.gleb.oauthservice.exception.InvalidRegisterClientRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RegisteredClientService {
    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponseModel addNewRegisteredClient(RegisterClientRequest registerClientRequest) {
        RegisteredClient byClientId = registeredClientRepository.findByClientId(registerClientRequest.getClientId());
        if (byClientId != null) {
            throw new InvalidRegisterClientRequest(String.format("%s_error, client with clientId: %s, already exists",
                    getClass().getSimpleName(), registerClientRequest.getClientId()));
        }
        var client = RegisteredClient.withId(UUID.randomUUID().toString())
                .tokenSettings(
                        TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofHours(1))
                                .refreshTokenTimeToLive(Duration.ofDays(90))
                                .build()
                )
                .clientId(registerClientRequest.getClientId())
                .clientSecret(passwordEncoder.encode(registerClientRequest.getClientSecret()))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri(registerClientRequest.getRedirectedUri())
                .scope(OidcScopes.OPENID)
                .build();
        try {
            registeredClientRepository.save(client);
        } catch (Exception e) {
            log.warn("{}_error, can`t save client with client_id: {}",
                    getClass().getSimpleName(), registerClientRequest.getClientId());
            throw new InvalidRegisterClientRequest(String.format("%s_error, can`t save client with clientId: %s",
                    getClass().getSimpleName(), registerClientRequest.getClientId()));
        }
        return ApiResponseModel.builder()
                .status(HttpStatus.OK)
                .description("Client successfully created")
                .build();
    }
}
