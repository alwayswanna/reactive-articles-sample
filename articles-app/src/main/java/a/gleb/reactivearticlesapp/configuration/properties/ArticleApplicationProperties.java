package a.gleb.reactivearticlesapp.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties("reactive")
@Validated
@Getter
@Setter
public class ArticleApplicationProperties {

    @NotNull
    private int fetchDataBefore;

    @NotNull
    private int checkArticlesIntervalDays;

    @NotNull
    private List<String> authorizedPaths;
}
