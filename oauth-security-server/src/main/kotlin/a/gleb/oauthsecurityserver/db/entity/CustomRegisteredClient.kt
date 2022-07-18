package a.gleb.oauthsecurityserver.db.entity

import lombok.Data
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.config.ClientSettings
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import java.time.Instant
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "oauth2_registered_client")
@Data
data class CustomRegisteredClient(

    @Id
    val id: String,
    val clientId: String,
    val clientIdIssuedAt: Instant,
    val clientSecret: String,
    val clientSecretExpiresA: Instant,
    val clientName: String,
    @ElementCollection
    val clientAuthenticationMethods: Set<ClientAuthenticationMethod>,
    @ElementCollection
    val authorizationGrantTypes: Set<AuthorizationGrantType>,
    @ElementCollection
    val redirectUris: Set<String>,
    @ElementCollection
    val scopes: Set<String>,
    val clientSettings: ClientSettings,
    val tokenSettings: TokenSettings
)