package a.gleb.articlecommon.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Account change model")
@Getter
@Setter
@Builder
public class AccountEditModel {

    @Schema(defaultValue = "Иван", description = "Account login")
    private String username;

    @Schema(defaultValue = "Qwerty123$", description = "New account password")
    private String password;

    @JsonProperty("first_name")
    @Schema(defaultValue = "Alex", description = "Change firstname")
    private String firstName;

    @JsonProperty("last_name")
    @Schema(defaultValue = "Griffin", description = "Change lastname")
    private String lastName;

    @Schema(defaultValue = "alex@yahoo.com", description = "Change email address")
    private String email;
}
