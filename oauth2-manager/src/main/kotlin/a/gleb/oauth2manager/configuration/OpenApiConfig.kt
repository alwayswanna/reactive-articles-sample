package a.gleb.oauth2manager.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import io.swagger.v3.oas.annotations.security.OAuthScope
import io.swagger.v3.oas.annotations.security.SecurityScheme

const val NAME_SECURITY_SCHEME = "securitySchemeOauth2"

@OpenAPIDefinition(
    info = Info(
        title = "article-reactive-backend",
        description = "API service for manage articles",
        version = "v1"
    )
)
@SecurityScheme(
    name = NAME_SECURITY_SCHEME,
    type = SecuritySchemeType.OAUTH2,
    flows = OAuthFlows(
        authorizationCode = OAuthFlow(
            authorizationUrl = "\${springdoc.oAuthFlow.authorizationUrl}",
            tokenUrl = "\${springdoc.oAuthFlow.tokenUrl}",
            scopes = [OAuthScope(name = "openid", description = "openid scope")]
        )
    )
)
class OpenApiConfig {}