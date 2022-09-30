package a.gleb.reactivearticlesapp.service;

import a.gleb.articlecommon.models.mq.AuthorChangeInfoEvent;
import a.gleb.articlecommon.models.rest.ApiResponseModel;
import a.gleb.articlecommon.models.rest.ArticleCreateRequestModel;
import a.gleb.articlecommon.models.rest.ArticleRequestModel;
import a.gleb.articlecommon.models.rest.ArticleResponseModel;
import a.gleb.reactivearticlesapp.configuration.properties.ArticleApplicationProperties;
import a.gleb.reactivearticlesapp.db.entity.Article;
import a.gleb.reactivearticlesapp.db.repository.ArticleRepository;
import a.gleb.reactivearticlesapp.exception.AccessDeniedException;
import a.gleb.reactivearticlesapp.exception.DataAccessException;
import a.gleb.reactivearticlesapp.mapper.ModelMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static a.gleb.reactivearticlesapp.service.ArticleAccessVerificationService.USERNAME_CLAIM;
import static a.gleb.reactivearticlesapp.service.ArticleAccessVerificationService.USER_ID_CLAIM;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.OK;

@Service
@Slf4j
@AllArgsConstructor
public class ArticleService {

    private static final String SUCCESS_STATUS_CODE = "Success";
    private static final String FAILURE_STATUS_CODE = "Failure";

    private ArticleRepository articleRepository;
    private ArticleApplicationProperties articleApplicationProperties;
    private ModelMapper modelMapper;
    private ArticleAccessVerificationService articleAccessVerificationService;


    /**
     * Method for RestAPI, create new article
     * If something goes wrong throw {@link DataAccessException} with message.
     *
     * @param article from user request
     * @return {@link ApiResponseModel} which contains new article
     */
    public Mono<ApiResponseModel> save(ArticleCreateRequestModel article, Authentication authentication) {
        var jwtPrincipal = (Jwt) authentication.getPrincipal();
        var articleToSave = modelMapper.toNewArticle(article);
        articleToSave.setAuthorId(UUID.fromString(jwtPrincipal.getClaim(USER_ID_CLAIM)));
        articleToSave.setAuthorLogin(jwtPrincipal.getClaim(USERNAME_CLAIM));
        return articleRepository.save(articleToSave)
                .flatMap(it -> Mono.just(
                        ApiResponseModel.builder()
                                .status(OK)
                                .code(SUCCESS_STATUS_CODE)
                                .description(String.format("Successfully added new article, with ID: %s", it.getId()))
                                .payload(List.of(modelMapper.toArticleResponseModel(it)))
                                .build())
                ).onErrorResume(e -> Mono.error(new DataAccessException(
                        String.format(
                                "%s, something wrong when save article: %s",
                                getClass().getSimpleName(),
                                article.getTitle()
                        )
                )));
    }


    /**
     * Method for RestAPI edit existing article.
     * If something wrong throw {@link DataAccessException}.
     * If the user is trying to edit a post that is not theirs throw {@link AccessDeniedException}.
     *
     * @param article article from user request;
     * @return {@link ApiResponseModel} with edited article
     */
    @SneakyThrows
    public Mono<ApiResponseModel> editArticle(ArticleRequestModel article, Authentication authentication) {
        if (articleAccessVerificationService.isAllowable(authentication, articleRepository.findById(article.getId()))) {
            return Mono.error(() -> new AccessDeniedException(
                    String.format(
                            "%s, you can not edit this article, %s",
                            getClass().getSimpleName(),
                            article.getTitle()
                    )
            ));
        }
        var jwtPrincipal = (Jwt) authentication.getPrincipal();
        var articleToSaveInDatabaseAfterEdit = modelMapper.toArticle(article);
        articleToSaveInDatabaseAfterEdit.setAuthorId(UUID.fromString(jwtPrincipal.getClaim(USER_ID_CLAIM)));
        articleToSaveInDatabaseAfterEdit.setAuthorLogin(jwtPrincipal.getClaim(USERNAME_CLAIM));
        return articleRepository.save(articleToSaveInDatabaseAfterEdit)
                .flatMap(it -> Mono.just(
                        ApiResponseModel.builder()
                                .status(OK)
                                .code(SUCCESS_STATUS_CODE)
                                .description(String.format("Successfully edit article, with ID: %s", it.getId()))
                                .payload(List.of(modelMapper.toArticleResponseModel(it)))
                                .build())
                ).onErrorResume(e -> Mono.error(new DataAccessException(
                        String.format(
                                "%s, error while edit existing article, %s",
                                getClass().getSimpleName(),
                                article.getTitle())
                )));
    }


    /**
     * Method for RestAPI which delete article by ID
     *
     * @param articleId from user request
     * @return {@link ApiResponseModel} with status of operation
     */
    @SneakyThrows
    public Mono<ApiResponseModel> remove(UUID articleId, Authentication authentication) {
        if (articleAccessVerificationService.isAllowable(authentication, articleRepository.findById(articleId))) {
            return Mono.error(() -> new AccessDeniedException(
                    String.format(
                            "%s, you can not remove this article, id: %s",
                            getClass().getSimpleName(),
                            articleId
                    )
            ));
        }
        articleRepository.deleteById(articleId).doOnError(e ->
                log.warn("{}_error, can`t remove article with ID: {}", getClass().getSimpleName(), articleId)
        );
        return Mono.just(
                ApiResponseModel.builder()
                        .status(OK)
                        .code(SUCCESS_STATUS_CODE)
                        .description(String.format("Successfully remove article, with ID: %s", articleId))
                        .build());
    }

    /**
     * Method for RestAPI which delete article by ID
     *
     * @param articleId from user request
     * @return {@link ApiResponseModel} with status of operation
     */
    public Mono<ApiResponseModel> remove(UUID articleId) {
        articleRepository.deleteById(articleId).doOnError(e ->
                log.warn("{}_error, can`t remove article with ID: {}", getClass().getSimpleName(), articleId)
        );
        return Mono.just(
                ApiResponseModel.builder()
                        .status(OK)
                        .code(SUCCESS_STATUS_CODE)
                        .description(String.format("Successfully remove article, with ID: %s", articleId))
                        .build());
    }


    /**
     * Method for RestAPI which fetch article by ID
     *
     * @param articleId from user request
     * @return {@link ApiResponseModel} with article
     */
    public Mono<ApiResponseModel> findById(UUID articleId) {
        return articleRepository.findById(articleId)
                .flatMap(it -> Mono.just(
                        ApiResponseModel.builder()
                                .status(OK)
                                .code(SUCCESS_STATUS_CODE)
                                .payload(List.of(modelMapper.toArticleResponseModel(it)))
                                .build())

                ).switchIfEmpty(
                        Mono.just(
                                ApiResponseModel.builder()
                                        .status(CONFLICT)
                                        .code(FAILURE_STATUS_CODE)
                                        .description(String.format("Cant find article with ID: %s", articleId))
                                        .build()
                        ));
    }


    /**
     * Method for RestAPI which fetch all articles
     *
     * @return {@link ApiResponseModel} with all fetched articles
     */
    public Mono<ApiResponseModel> findAll() {
        return articleRepository.findAll()
                .collectList()
                .flatMap(it -> {
                    var listOfArticles = new ArrayList<ArticleResponseModel>();
                    it.forEach(article -> listOfArticles.add(modelMapper.toArticleResponseModel(article)));
                    return Mono.just(
                            ApiResponseModel.builder()
                                    .status(OK)
                                    .code(SUCCESS_STATUS_CODE)
                                    .description("Success fetch all articles")
                                    .payload(listOfArticles)
                                    .build());
                })
                .switchIfEmpty(
                        Mono.just(
                                ApiResponseModel.builder()
                                        .status(CONFLICT)
                                        .code(FAILURE_STATUS_CODE)
                                        .description("Failure fetch all articles")
                                        .build()
                        )
                );
    }


    /**
     * Method for RestAPI which fetch part of articles
     *
     * @return {@link ApiResponseModel} with part of articles
     */
    public Mono<ApiResponseModel> fetchPartOfArticles() {
        LocalDateTime lastUpdate = LocalDateTime.now()
                .minusDays(articleApplicationProperties.fetchDataBefore());
        return articleRepository.findArticleByLastUpdateIsAfter(lastUpdate)
                .collectList()
                .flatMap(it -> {
                    var listOfArticles = new ArrayList<ArticleResponseModel>();
                    it.forEach(article -> listOfArticles.add(modelMapper.toArticleResponseModel(article)));
                    return Mono.just(
                            ApiResponseModel.builder()
                                    .status(OK)
                                    .code(SUCCESS_STATUS_CODE)
                                    .description("Success fetch all articles")
                                    .payload(listOfArticles)
                                    .build());
                })
                .switchIfEmpty(
                        Mono.just(
                                ApiResponseModel.builder()
                                        .status(CONFLICT)
                                        .code(FAILURE_STATUS_CODE)
                                        .description("Failure fetch articles by last week")
                                        .build()
                        )
                );
    }


    /**
     * Method for fetch articles for make every day verification
     *
     * @param days from config
     */
    public Flux<Article> findArticlesIsAfter(int days) {
        return articleRepository.findArticleByLastUpdateIsAfter(LocalDateTime.now().minusDays(days))
                .doOnError(e -> {
                    log.warn("{}_error, can`t fetch articles, message {}", getClass().getSimpleName(), e.getMessage());
                    throw new DataAccessException(String.format(
                            "DataObjectAccessException, can not fetch post by where update_time is after: %s",
                            days
                    ));
                });
    }


    /**
     * Method for fetch articles for make monthly verification
     */
    public Flux<Article> findAllArticles() {
        return articleRepository.findAll().doOnError(e -> {
            log.warn("{}_error, can`t fetch all articles, message {}", getClass().getSimpleName(), e.getMessage());
            throw new DataAccessException("DataObjectAccessException, can not fetch all articles");
        });
    }


    /**
     * Handle event from oauth2-manager-service, and change all articles which related with author.
     *
     * @param event from oauth2-manager-service
     * @return {@link Mono<Void>}
     */
    public Flux<Article> changeArticleInfo(AuthorChangeInfoEvent event) {
        return articleRepository.findAllByAuthorId(event.getAccountId())
                .map(article -> {
                    article.setAuthorLogin(event.getNewUsername());
                    article.setLastUpdate(event.getTimestamp());
                    return article;
                })
                .flatMap(articleRepository::save)
                .doOnError(it ->
                        log.warn("{}_warn, error, while update article for user with accountId: {}, message: {}",
                                getClass().getSimpleName(),
                                event.getAccountId(),
                                it.getMessage())
                );
    }

    public Flux<Void> removeRelatedArticles(AuthorChangeInfoEvent event) {
        return articleRepository.findAllByAuthorId(event.getAccountId())
                .flatMap(article -> articleRepository.delete(article))
                .doOnError(it ->
                        log.warn("{}_warn, error, while delete article for user with accountId: {}, message: {}",
                                getClass().getSimpleName(),
                                event.getAccountId(),
                                it.getMessage()
                        )
                );
    }

}
