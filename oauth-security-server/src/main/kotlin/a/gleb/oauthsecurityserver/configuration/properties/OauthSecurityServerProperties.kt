package a.gleb.oauthsecurityserver.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import org.springframework.web.cors.CorsConfiguration

@ConstructorBinding
@ConfigurationProperties("oauth")
@Validated
data class OauthSecurityServerProperties(
    val issuerUrl: String,
    val defaultClient: DefaultClient,
    val defaultUser: DefaultUser,
    val cors: Cors
)

@ConstructorBinding
data class DefaultUser(
    val defaultUsername: String,
    val defaultPassword: String,
    val defaultEmail: String
)

@ConstructorBinding
data class DefaultClient(
    val defaultClientId: String,
    val defaultClientSecret: String,
    val defaultAccessTokenTimeToLive: Long,
    val defaultRefreshTokenTimeToLive: Long,
    val defaultRedirectUris: List<String>
)

class Cors : CorsConfiguration()
