package a.gleb.reactivearticlesapp.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;
import static org.springframework.security.oauth2.jwt.Jwt.withTokenValue;

public class TestSecurityContextFactoryInterceptor implements WithSecurityContextFactory<WithMockedUser> {

    private static final String MUST_BE_UNUSED = "must-be-unused";
    private static final String CLAIM_PREFERRED_USERNAME = "preferred_username";
    private static final String NAME = "username";
    private static final String MASTER_ID_CLAIM = "user_id";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public SecurityContext createSecurityContext(WithMockedUser annotation) {
        var jwt = withTokenValue(MUST_BE_UNUSED)
                .header(MUST_BE_UNUSED, MUST_BE_UNUSED)
                .claim(CLAIM_PREFERRED_USERNAME, annotation.value())
                .claim(MASTER_ID_CLAIM, annotation.masterId())
                .claim(NAME, annotation.name())
                .build();

        var grantedAuthorities = Arrays.stream(annotation.role())
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX.concat(role)))
                .collect(toList());
        var ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new JwtAuthenticationToken(jwt, grantedAuthorities));
        return ctx;
    }
}
