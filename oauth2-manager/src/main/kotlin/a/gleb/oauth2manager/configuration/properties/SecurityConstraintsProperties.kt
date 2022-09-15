package a.gleb.oauth2manager.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("oauth2-manager")
@ConstructorBinding
@Validated
data class SecurityConstraintsProperties(
    val securityConstraints: List<SecurityConstraint>,
    val securityExclusions: List<SecurityExclusion>
)

@ConstructorBinding
data class SecurityConstraint(
    val securityCollections: List<SecurityCollection>,
    val roles: List<String>
)

@ConstructorBinding
data class SecurityCollection(
    val patterns: List<String>,
    val methods: List<String>?
)

@ConstructorBinding
data class SecurityExclusion(
    val methods: List<String>?,
    val patterns: List<String>
)