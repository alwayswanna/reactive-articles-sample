package a.gleb.articlecommon.models.rest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
public class RegistrationModel {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String role;

    @NotNull
    @Email
    private String email;
}
