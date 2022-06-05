package a.gleb.oauthservice.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Validated
@Getter
@Setter
@ConfigurationProperties("oauth")
public class OauthServiceConfigurationProperties {
    @NotBlank
    private String issuerUrl;
    @NotBlank
    private String defaultUsername;
    @NotBlank
    private String defaultPassword;
    @NotBlank
    @Email
    private String defaultEmail;
    @NotBlank
    private String clientSecret;
}
