package a.gleb.reactivearticlesapp.configuration;

import a.gleb.reactivearticlesapp.configuration.properties.OpenApiConfigurationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@Slf4j
@EnableConfigurationProperties(OpenApiConfigurationProperties.class)
public class SwaggerApiConfiguration {

    @Bean
    public OpenAPI openAPI(OpenApiConfigurationProperties openApiConfigurationProperties) {
        return new OpenAPI()
                .components(new Components())
                .info(
                        new Info()
                                .title("reactive-sample-project")
                                .description("Spring boot reactive blog backend")
                                .termsOfService(StringUtils.EMPTY)
                                .version(getBuildVersion())
                )
                .servers(getServers(openApiConfigurationProperties.getServerUrls()));
    }


    @SneakyThrows
    private String getBuildVersion() {
        InputStream ins = null;
        try {
            var properties = new Properties();
            ins = getClass().getResourceAsStream("/META-INF/build-info.properties");
            properties.load(ins);
            return properties.getProperty("build.version");
        } catch (Exception e) {
            log.error("{}_error, can`t load resource with build.version", getClass().getSimpleName());
        } finally {
            if (ins != null) {
                ins.close();
            }
        }
        return StringUtils.EMPTY;
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
