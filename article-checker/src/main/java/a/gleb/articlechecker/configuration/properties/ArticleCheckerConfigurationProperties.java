package a.gleb.articlechecker.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@Getter
@Setter
@ConfigurationProperties("checker")
public class ArticleCheckerConfigurationProperties {

    /**
     * Dictionary with black list words
     */
    @NotNull
    private List<String> dictionaryBlackListWords;
}
