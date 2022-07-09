package a.gleb.oauthsecurityserver.configuration

import a.gleb.oauthsecurityserver.configuration.properties.OauthSecurityServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableConfigurationProperties(OauthSecurityServerProperties::class)
class OauthServiceConfiguration : WebMvcConfigurer{

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedMethods("GET", "POST")
        registry.addMapping("/oauth/**")
            .allowedMethods("GET", "POST")
    }
}