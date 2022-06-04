package a.gleb.reactivearticlesapp.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties("springdoc")
@Validated
@Data
public class OpenApiConfigurationProperties {

    @NotNull
    private List<String> serverUrls;
}
