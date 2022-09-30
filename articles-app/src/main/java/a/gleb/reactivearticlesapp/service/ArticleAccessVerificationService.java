package a.gleb.reactivearticlesapp.service;

import a.gleb.reactivearticlesapp.db.entity.Article;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@AllArgsConstructor
public class ArticleAccessVerificationService {

    public static final String USER_ID_CLAIM = "user_id";
    public static final String USERNAME_CLAIM = "username";

    @SneakyThrows
    public boolean isAllowable(Authentication authentication, Mono<Article> article)
            throws ExecutionException {
        var principal = (Jwt) authentication.getPrincipal();
        return article.map(it -> it.getAuthorId().toString()
                .equalsIgnoreCase(principal.getClaimAsString(USERNAME_CLAIM))).toFuture().get() &&
                article.map(it -> it.getAuthorLogin()
                        .equalsIgnoreCase(principal.getClaimAsString(USERNAME_CLAIM))).toFuture().get();

    }

}
