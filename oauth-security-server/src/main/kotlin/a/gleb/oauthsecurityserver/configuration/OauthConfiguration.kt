package a.gleb.oauthsecurityserver.configuration

import a.gleb.oauthsecurityserver.configuration.properties.OauthSecurityServerProperties
import a.gleb.oauthsecurityserver.db.entity.Account
import a.gleb.oauthsecurityserver.db.entity.AccountRoles
import a.gleb.oauthsecurityserver.db.repository.AccountRepository
import a.gleb.oauthsecurityserver.service.OauthUserDetailsService
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import java.util.*


@Service
class OauthConfiguration(
    var properties: OauthSecurityServerProperties,
    var accountRepository: AccountRepository,
    var oauthUserDetailsService: OauthUserDetailsService
) {

    companion object {
        fun generateRsaKey(): KeyPair {
            var keyPair: KeyPair
            try {
                val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
                keyPairGenerator.initialize(2048)
                keyPair = keyPairGenerator.generateKeyPair()
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
            return keyPair
        }
    }

    @Bean
    @Order(-1)
    fun authServiceSecurityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity)
        httpSecurity.exceptionHandling { ex ->
            ex.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
        }
        return httpSecurity.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity.authorizeHttpRequests { it.anyRequest().authenticated() }
            .formLogin(Customizer.withDefaults())
        return httpSecurity.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(passwordEncoder: PasswordEncoder): DaoAuthenticationProvider {
        var provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder)
        provider.setUserDetailsService(oauthUserDetailsService)
        return provider
    }

    @Bean
    fun providerSettings(): ProviderSettings {
        return ProviderSettings.builder().issuer(properties.issuerUrl).build()
    }

    @Bean
    fun registerClientRepository(
        jdbcTemplate: JdbcTemplate,
        passwordEncoder: PasswordEncoder
    ): RegisteredClientRepository {
        val client = RegisteredClient.withId(UUID.randomUUID().toString())
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofHours(properties.defaultClient.defaultAccessTokenTimeToLive))
                    .refreshTokenTimeToLive(Duration.ofDays(properties.defaultClient.defaultRefreshTokenTimeToLive))
                    .build()
            )
            .clientId(properties.defaultClient.defaultClientId)
            .clientSecret(passwordEncoder.encode(properties.defaultClient.defaultClientSecret))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope(OidcScopes.OPENID)

        for (uri in properties.defaultClient.defaultRedirectUris) {
            client.redirectUri(uri)
        }
        val clientToSave = client.build()

        val registerClientRepository = JdbcRegisteredClientRepository(jdbcTemplate)
        if (registerClientRepository.findByClientId(clientToSave.clientId) == null) {
            registerClientRepository.save(clientToSave)
        }
        return registerClientRepository
    }

    @Bean
    fun authorizationService(
        jdbcTemplate: JdbcTemplate,
        repository: RegisteredClientRepository
    ): OAuth2AuthorizationService {
        return JdbcOAuth2AuthorizationService(jdbcTemplate, repository)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyPair: KeyPair = generateRsaKey()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val rsaKey = RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build()
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet(jwkSet)
    }

    @Bean
    fun createDefaultUserAtStartUp(passwordEncoder: PasswordEncoder) = CommandLineRunner {
        val defaultAccount = Account(
            id = UUID.randomUUID(),
            username = properties.defaultUser.defaultUsername,
            password = passwordEncoder.encode(properties.defaultUser.defaultPassword),
            email = properties.defaultUser.defaultEmail,
            enabled = true,
            roles = AccountRoles.ADMIN
        )
        if (!accountRepository.findAccountByUsernameOrEmail(defaultAccount.username, defaultAccount.email).isPresent) {
            accountRepository.save(defaultAccount)
        }
    }
}