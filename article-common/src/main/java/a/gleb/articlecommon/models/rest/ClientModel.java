package a.gleb.articlecommon.models.rest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
public class ClientModel {
    private String clientId;
    private Set<String> redirectUris;
    private Set<String> clientAuthenticationMethods;
    private Set<String> clientAuthorizationGrantTypes;
}
