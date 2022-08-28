package a.gleb.oauth2manager.configuration

import a.gleb.oauth2manager.configuration.properties.SecurityConstraintsProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

const val ROLE_PREFIX = "ROLE_"

val SYSTEM_UNPROTECTED_PATTERNS = listOf(
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/webjars/swagger-ui/**"
)

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val properties: SecurityConstraintsProperties
) {

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf().disable()
            .authorizeRequests { restrictAPIMethods(it) }
            .oauth2ResourceServer()
            .jwt {
                var jwtAuthenticationConverter = JwtAuthenticationConverter()
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(SpringBootOAuth2JwtConverter())
                it.jwtAuthenticationConverter(jwtAuthenticationConverter)
            }
        return httpSecurity.build()
    }

    private fun restrictAPIMethods(
        matcherRegistry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry
    ) {
        /* permit all */
        matcherRegistry.antMatchers(*SYSTEM_UNPROTECTED_PATTERNS.toTypedArray()).permitAll()
        if (properties.securityExclusions.isNotEmpty()) {
            properties.securityExclusions.forEach {
                it.methods?.forEach { httpMethod ->
                    matcherRegistry.antMatchers(HttpMethod.valueOf(httpMethod), *it.patterns.toTypedArray())
                        .permitAll()
                }
            }
        }
        /* protected patterns and methods */
        properties.securityConstraints.forEach { securityConstraint ->
            val authRoles = securityConstraint.roles.toTypedArray()
            securityConstraint.securityCollections.forEach { securityCollection ->
                val patterns = securityCollection.patterns.toTypedArray()
                securityCollection.methods?.forEach { method ->
                    matcherRegistry.antMatchers(HttpMethod.valueOf(method), *patterns).hasAnyRole(*authRoles)
                } ?: matcherRegistry.antMatchers(*patterns).hasAnyRole(*authRoles)
            }
        }
        /* secure any request */
        matcherRegistry.anyRequest().authenticated()
    }


}

class SpringBootOAuth2JwtConverter : Converter<Jwt, MutableCollection<GrantedAuthority>> {

    override fun convert(source: Jwt): MutableCollection<GrantedAuthority>? {
        val grantedAuthorityList = source.getClaim<Any>("role")
            .toString()
            .replace("[", "")
            .replace("]", "")
            .split(",")
            .map { it.replace("\"", "") }
            .map { ROLE_PREFIX + it }
            .map { SimpleGrantedAuthority(it) }

        return grantedAuthorityList.toMutableList()
    }
}