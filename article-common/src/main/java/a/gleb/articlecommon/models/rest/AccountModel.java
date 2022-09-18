package a.gleb.articlecommon.models.rest;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Schema(description = "New account model")
@Getter
@Setter
@Builder
public class AccountModel {

    @NotNull
    @Schema(defaultValue = "Иван", required = true, description = "Account username/login")
    private String username;

    @NotNull
    @Schema(defaultValue = "Qwerty123$", required = true, description = "Account password")
    private String password;

    @JsonProperty("first_name")
    @Schema(defaultValue = "Alex", description = "User firstname")
    private String firstName;

    @JsonProperty("last_name")
    @Schema(defaultValue = "Griffin", description = "User lastname")
    private String lastName;

    @NotNull
    @Schema(defaultValue = "alexey@yahoo.com", required = true, description = "Account email address")
    private String email;
}
