package a.gleb.reactivearticlesapp.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties("springdoc")
@Validated
public record OpenApiConfigurationProperties(
        @NotNull List<String> serverUrls,
        @NotNull String authorizationUrl
) {}
