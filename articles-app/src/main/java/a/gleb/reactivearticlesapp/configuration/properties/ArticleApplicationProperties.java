package a.gleb.reactivearticlesapp.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties("reactive")
@Validated
public record ArticleApplicationProperties(
        @NotNull int fetchDataBefore,
        @NotNull int checkArticlesIntervalDays,
        @NotNull List<String> authorizedPaths,
        @NotNull List<String> roles
) {
}
