package a.gleb.reactivearticlesapp.security;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestSecurityContextFactoryInterceptor.class)
public @interface WithMockedUser {

    @AliasFor("value")
    String name() default "user";

    @AliasFor("name")
    String value() default "user";

    String[] role() default {"USER"};

    String masterId() default "ffffd9ba-8a30-4e65-bc91-20a3ba0f28de";
}
