package a.gleb.reactivearticlesapp.configuration;

import a.gleb.reactivearticlesapp.configuration.properties.ArticleApplicationProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerReactiveAuthenticationManagerResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableConfigurationProperties(ArticleApplicationProperties.class)
@EnableWebFluxSecurity
public class ArticleApplicationConfig {
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ArticleApplicationProperties properties) {
        JwtIssuerReactiveAuthenticationManagerResolver authenticationManagerResolver = new JwtIssuerReactiveAuthenticationManagerResolver
                ("http://localhost:9001");
        return http
                .cors().disable().csrf().disable()
                .authorizeExchange(exchangeSpec -> {
                    exchangeSpec.pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                            "/webjars/swagger-ui/**", "/actuator/**").permitAll();
                    exchangeSpec.pathMatchers(properties.getAuthorizedPaths().toArray(String[]::new)).authenticated();
                })
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> {
                    oAuth2ResourceServerSpec.authenticationManagerResolver(authenticationManagerResolver);
                })
                .build();
    }
}
