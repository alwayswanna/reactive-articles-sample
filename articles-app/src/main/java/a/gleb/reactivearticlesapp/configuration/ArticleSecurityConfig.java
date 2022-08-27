package a.gleb.reactivearticlesapp.configuration;

import a.gleb.reactivearticlesapp.configuration.properties.ArticleApplicationProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@EnableConfigurationProperties(ArticleApplicationProperties.class)
@EnableWebFluxSecurity
public class ArticleSecurityConfig {

    private static List<String> DEFAULT_UNPROTECTED_PATTERNS = new ArrayList<>(Arrays.asList("/swagger-ui.html",
            "/swagger-ui/**", "/v3/api-docs/**", "/webjars/swagger-ui/**", "/actuator/**"));


    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ArticleApplicationProperties properties
    ) {
        http
                .csrf().disable()
                .authorizeExchange(exchangeSpec -> restrictApiMethods(exchangeSpec, properties))
                .oauth2ResourceServer()
                .jwt(jwtSpec -> {
                    var reactiveAuthConverter = new ReactiveJwtAuthenticationConverter();
                    reactiveAuthConverter.setJwtGrantedAuthoritiesConverter(new SpringBootReactiveAuthConverter());
                    jwtSpec.jwtAuthenticationConverter(reactiveAuthConverter);
                });
        return http.build();
    }

    private void restrictApiMethods(AuthorizeExchangeSpec exchangeSpec, ArticleApplicationProperties properties) {

        exchangeSpec.pathMatchers(DEFAULT_UNPROTECTED_PATTERNS.toArray(String[]::new)).permitAll();

        properties.securityConstraints().forEach(securityConstraint -> {
            var roles = securityConstraint.getRoles();
            securityConstraint.getSecurityCollections().forEach(securityCollections -> {
                var patterns = securityCollections.getPatterns().toArray(String[]::new);
                if (securityCollections.getMethods() == null || securityCollections.getMethods().isEmpty()) {
                    exchangeSpec.pathMatchers(patterns).hasAnyRole(roles.toArray(String[]::new));
                    return;
                }
                securityCollections.getMethods().forEach(method -> {
                    exchangeSpec.pathMatchers(HttpMethod.valueOf(method), patterns)
                            .hasAnyRole(roles.toArray(String[]::new));
                });
            });
        });

        exchangeSpec.anyExchange().authenticated();
    }

    private class SpringBootReactiveAuthConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

        private static final String ROLE_PREFIX = "ROLE_%s";

        @Override
        public Flux<GrantedAuthority> convert(Jwt source) {

            Stream<GrantedAuthority> rolesStream = Arrays.stream(
                            source.getClaims().get("role")
                                    .toString()
                                    .replaceAll("\\[", "")
                                    .replaceAll("\\]", "")
                                    .split(","))
                    .map(rolesWithSymbols -> rolesWithSymbols.replaceAll("\\W", EMPTY))
                    .map(role -> String.format(ROLE_PREFIX, role))
                    .map(role -> new SimpleGrantedAuthority(role));

            return Flux.fromStream(
                    rolesStream
            );
        }
    }
}
