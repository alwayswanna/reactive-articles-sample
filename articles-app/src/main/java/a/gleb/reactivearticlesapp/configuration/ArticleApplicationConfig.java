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

//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .mvcMatcher("/**")
//                .authorizeRequests()
//                .mvcMatchers("/api/**").access("hasAuthority('SCOPE_message.read')")
//                .and()
//                .oauth2ResourceServer()
//                .jwt();
//        return http.build();
//    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        JwtIssuerReactiveAuthenticationManagerResolver authenticationManagerResolver = new JwtIssuerReactiveAuthenticationManagerResolver
                ("http://localhost:8081");
//        http
//                .authorizeExchange(exchanges ->
//                        exchanges
//                                .pathMatchers("/*", "/api/*").hasAnyAuthority()
//                                .anyExchange().authenticated()
//                )
//                .oauth2ResourceServer(oauth2ResourceServer ->
//                        oauth2ResourceServer
//                                .authenticationManagerResolver(authenticationManagerResolver)
//                );
        return http.build();
    }
}
