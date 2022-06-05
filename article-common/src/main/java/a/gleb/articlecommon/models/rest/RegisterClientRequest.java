package a.gleb.articlecommon.models.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Validated
public class RegisterClientRequest {
    @NotBlank
    private String clientId;
    @NotBlank
    private String clientSecret;
    @NotBlank
    private String redirectedUri;
}
