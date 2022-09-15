package a.gleb.reactivearticlesapp.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("article-app")
@Validated
public record ArticleApplicationProperties(
        @NotNull int fetchDataBefore,
        @NotNull int checkArticlesIntervalDays,
        @NotNull List<String> unprotectedPatterns,
        @NotNull List<SecurityConstraint> securityConstraints
) {

    @Getter
    @Setter
    @ConstructorBinding
    public static class SecurityConstraint {

        @NotNull
        List<SecurityCollections> securityCollections = new ArrayList<>();
        @NotNull
        private List<String> roles = new ArrayList<>();
    }

    @Getter
    @Setter
    @ConstructorBinding
    public static class SecurityCollections {

        @NotNull
        private List<String> patterns = new ArrayList<>();
        private List<String> methods = new ArrayList<>();
    }
}

