package a.gleb.reactivearticlesapp.configuration;

import a.gleb.reactivearticlesapp.configuration.properties.OpenApiConfigurationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@EnableConfigurationProperties(OpenApiConfigurationProperties.class)
public class SwaggerApiConfiguration {

    public static final String NAME_SECURITY_SCHEMA = "myOauth2Security";

    @Bean
    public OpenAPI openAPI(OpenApiConfigurationProperties properties, BuildProperties buildProperties) {
        UriComponents tokenURL = UriComponentsBuilder.fromHttpUrl("http://localhost:9001")
                .pathSegment("oauth2/token")
                .build();
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(NAME_SECURITY_SCHEMA,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(
                                                new OAuthFlows()
                                                        .authorizationCode(
                                                                new OAuthFlow()
                                                                        .authorizationUrl(properties.authorizationUrl())
                                                                        .tokenUrl(tokenURL.toString())
                                                                        .refreshUrl(tokenURL.toString())
                                                                        .scopes(new Scopes()
                                                                                .addString("openid", "openid")))
                                                )
                                        )
                        )
                .info(
                        new Info()
                                .title("reactive-sample-project")
                                .description("Spring boot reactive blog backend")
                                .termsOfService(StringUtils.EMPTY)
                                .version(buildProperties.getVersion())
                )
                .servers(getServers(properties.serverUrls()));
    }

    private List<Server> getServers(List<String> serverUrls) {
        String hostName = InetAddress.getLoopbackAddress().getHostName();

        List<io.swagger.v3.oas.models.servers.Server> servers = new ArrayList<>();
        for (String serverUrl : serverUrls) {
            io.swagger.v3.oas.models.servers.Server server = new io.swagger.v3.oas.models.servers.Server()
                    .url(serverUrl);

            if (serverUrl.contains(hostName)) {
                log.info("{}_info, there are server urls: {}", getClass().getSimpleName(), server.getUrl());
                servers.add(0, server);

            } else {
                servers.add(server);

            }
        }
        return servers;
    }
}
