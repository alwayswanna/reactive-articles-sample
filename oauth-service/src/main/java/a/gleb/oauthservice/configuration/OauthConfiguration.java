package a.gleb.oauthservice.configuration;

import a.gleb.oauthservice.configuration.properties.OauthServiceConfigurationProperties;
import a.gleb.oauthservice.db.entity.Account;
import a.gleb.oauthservice.db.entity.AccountRoles;
import a.gleb.oauthservice.db.repository.AccountRepository;
import a.gleb.oauthservice.service.OauthUserDetailsService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

@Configuration
@AllArgsConstructor
@Slf4j
public class OauthConfiguration {
    public static final String DEFAULT_CLIENT_ID = "message";
    private final OauthServiceConfigurationProperties properties;
    private final AccountRepository accountRepository;
    private final OauthUserDetailsService oauthUserDetailsService;

    @Bean
    @Order(-1)
    public SecurityFilterChain authServiceSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        httpSecurity.exceptionHandling((ex) -> ex.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));
        return httpSecurity.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(it -> it.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(oauthUserDetailsService);
        return provider;
    }

    /**
     * On startup create
     * @param jdbcTemplate
     * @param passwordEncoder
     * @return
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        var client  = RegisteredClient.withId(UUID.randomUUID().toString())
                .tokenSettings(
                        TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofHours(1))
                                .refreshTokenTimeToLive(Duration.ofDays(90))
                                .build()
                )
                .clientId(DEFAULT_CLIENT_ID)
                .clientSecret(passwordEncoder.encode(properties.getClientSecret()))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:8081/authorized")
                .scope(OidcScopes.OPENID)
                .build();

        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        if (registeredClientRepository.findByClientId(client.getClientId()) == null){
            registeredClientRepository.save(client);
        }
        return registeredClientRepository;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }


    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }


    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().issuer(properties.getIssuerUrl()).build();
    }


    /**
     * Create default account on startup. Account params takes from config .yaml.
     */
    @Bean
    public CommandLineRunner createUserAtStartUp(){
        log.info("START adding default user");
        var defaultAccount = new Account();
        defaultAccount.setId(UUID.randomUUID());
        defaultAccount.setUsername(properties.getDefaultUsername());
        defaultAccount.setEmail(properties.getDefaultEmail());
        defaultAccount.setPassword(passwordEncoder().encode(properties.getDefaultPassword()));
        defaultAccount.setEnabled(true);
        defaultAccount.setRole(AccountRoles.ADMIN);
        return (args -> {
           if (!accountRepository.
                   findAccountByUsernameOrEmail(defaultAccount.getUsername(), defaultAccount.getEmail()).isPresent())
           {
               accountRepository.save(defaultAccount);
               log.info("{}_info, added new user in database: userId {}", getClass().getSimpleName(), defaultAccount.getId());
           }
            log.info("END adding default user");
        });
    }
}
