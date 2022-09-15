package a.gleb.oauthsecurityserver.configuration

import a.gleb.oauthsecurityserver.configuration.properties.OauthSecurityServerProperties
import a.gleb.oauthsecurityserver.db.entity.Account
import a.gleb.oauthsecurityserver.db.entity.AccountRoles.ADMIN
import a.gleb.oauthsecurityserver.db.entity.AccountRoles.USER
import a.gleb.oauthsecurityserver.db.repository.AccountRepository
import a.gleb.oauthsecurityserver.service.AccountService
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
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType.*
import org.springframework.security.oauth2.core.ClientAuthenticationMethod.*
import org.springframework.security.oauth2.core.oidc.OidcScopes.OPENID
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.stereotype.Service
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
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
    var oauthUserDetailsService: OauthUserDetailsService,
    var accountService: AccountService,
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
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(
            httpSecurity
                .cors {
                    val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
                    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", properties.cors)
                    it.configurationSource(urlBasedCorsConfigurationSource)
                }
        )
        httpSecurity.exceptionHandling { ex ->
            ex.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
        }
        return httpSecurity.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity.cors {
            val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
            urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", properties.cors)
            it.configurationSource(urlBasedCorsConfigurationSource)
        }
        httpSecurity.authorizeHttpRequests { it.anyRequest().authenticated() }
            .formLogin(Customizer.withDefaults())
        return httpSecurity.build()
    }


    @Bean
    fun authenticationProvider(passwordEncoder: PasswordEncoder): DaoAuthenticationProvider {
        var provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder)
        provider.setUserDetailsService(oauthUserDetailsService)
        return provider
    }


    /**
     * Sets issuer uri in token.
     */
    @Bean
    fun providerSettings(): ProviderSettings {
        return ProviderSettings.builder().issuer(properties.issuerUrl).build()
    }


    /**
     * Create default client, with default params if this client does not exist.
     * @param passwordEncoder for crypt client secret.
     * @param jdbcTemplate bean which work with database.
     */
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
            .clientAuthenticationMethod(CLIENT_SECRET_BASIC)
            .clientAuthenticationMethod(CLIENT_SECRET_POST)
            .clientAuthenticationMethod(CLIENT_SECRET_JWT)
            .clientAuthenticationMethod(PRIVATE_KEY_JWT)
            .clientAuthenticationMethod(NONE)
            .authorizationGrantType(AUTHORIZATION_CODE)
            .authorizationGrantType(REFRESH_TOKEN)
            .authorizationGrantType(CLIENT_CREDENTIALS)
            .scope(OPENID)

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


    /**
     * [Bean] for save sessions in database.
     * @param jdbcTemplate current jdbc bean.
     * @param repository repository which work with clients.
     */
    @Bean
    fun authorizationService(
        jdbcTemplate: JdbcTemplate,
        repository: RegisteredClientRepository
    ): OAuth2AuthorizationService {
        return JdbcOAuth2AuthorizationService(jdbcTemplate, repository)
    }


    /**
     * Using for generate JWT.
     */
    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyPair: KeyPair = generateRsaKey()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val rsaKey = RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build()
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet(jwkSet)
    }


    /**
     * Token customizer, adds roles [Account.roles] to token, for restrict rights on client systems.
     */
    @Bean
    fun jwtCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext?>? {
        return OAuth2TokenCustomizer {
            var principal = it!!.getPrincipal<Authentication?>()
            var userRoles = principal.authorities
                .asSequence()
                .map { role -> role.authority.toString() }
                .toList()
            it.claims.claim(
                "role",
                userRoles
            )
            accountService.enrichJWTByUserInfo(principal.name)
                .forEach { (key, value) -> it.claims.claim(key, value) }
        }
    }


    /**
     * [Bean] which create default user when authorization server is up,
     * if user does not exist.
     */
    @Bean
    fun createDefaultUserAtStartUp(passwordEncoder: PasswordEncoder) = CommandLineRunner {
        val defaultAccount = Account(
            id = UUID.randomUUID(),
            username = properties.defaultUser.defaultUsername,
            password = passwordEncoder.encode(properties.defaultUser.defaultPassword),
            email = properties.defaultUser.defaultEmail,
            enabled = true,
            roles = ADMIN.name.plus(',').plus(USER.name)
        )
        if (!accountRepository.findAccountByUsernameOrEmail(defaultAccount.username, defaultAccount.email).isPresent) {
            accountRepository.save(defaultAccount)
        }
    }
}