package a.gleb.oauthsecurityserver.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@ConstructorBinding
@ConfigurationProperties("oauth")
@Validated
data class OauthSecurityServerProperties(
    val issuerUrl: String,
    val defaultUsername: String,
    val defaultPassword: String,
    val defaultEmail: String,
    val clientSecret: String
)
