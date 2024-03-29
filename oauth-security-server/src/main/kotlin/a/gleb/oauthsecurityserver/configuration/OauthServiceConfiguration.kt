package a.gleb.oauthsecurityserver.configuration

import a.gleb.oauthsecurityserver.configuration.properties.OauthSecurityServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(OauthSecurityServerProperties::class)
class OauthServiceConfiguration {

    /**
     * [PasswordEncoder] which will be crypt client secret, which saves in database.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(13)
    }
}