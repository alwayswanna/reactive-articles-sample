package a.gleb.reactivearticlesapp.configuration;

import a.gleb.reactivearticlesapp.configuration.properties.ArticleApplicationProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@EnableConfigurationProperties(ArticleApplicationProperties.class)
@EnableWebFluxSecurity
@Slf4j
@AllArgsConstructor
public class ArticleApplicationConfig {


    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ArticleApplicationProperties properties
    ) {
        http
                .cors().and()
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
        exchangeSpec
                .pathMatchers(properties.authorizedPaths().toArray(String[]::new))
                .hasAnyRole(properties.roles().stream().toArray(String[]::new));
        exchangeSpec.anyExchange().permitAll();
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
            var fluxGrantedAuth = Flux.fromStream(
                    rolesStream
            );
            return fluxGrantedAuth;
        }
    }
}
