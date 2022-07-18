package a.gleb.reactivearticlesapp.service;

import a.gleb.articlecommon.models.db.Article;
import a.gleb.articlecommon.models.rest.ApiResponseModel;
import a.gleb.articlecommon.models.rest.ArticleCreateRequestModel;
import a.gleb.articlecommon.models.rest.ArticleRequestModel;
import a.gleb.articlecommon.models.rest.ArticleResponseModel;
import a.gleb.reactivearticlesapp.configuration.properties.ArticleApplicationProperties;
import a.gleb.reactivearticlesapp.exception.DataAccessException;
import a.gleb.reactivearticlesapp.mapper.ModelMapper;
import a.gleb.reactivearticlesapp.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.OK;

@Service
@Slf4j
@AllArgsConstructor
public class ArticleService {

    private ArticleRepository articleRepository;
    private ArticleApplicationProperties articleApplicationProperties;
    private ModelMapper modelMapper;


    /**
     * Method for RestAPI, create new article
     *
     * @param article from user request
     * @return {@link ApiResponseModel} which contains new article
     */
    public Mono<ApiResponseModel> save(ArticleCreateRequestModel article) {
        return articleRepository.save(modelMapper.toNewArticle(article))
                .flatMap(it -> {
                    return Mono.just(
                            ApiResponseModel.builder()
                                    .status(OK)
                                    .code("Success")
                                    .description(String.format("Successfully added new article, with ID: %s", it.getId()))
                                    .payload(List.of(modelMapper.toArticleResponseModel(it)))
                                    .build());
                })
                .switchIfEmpty(
                        Mono.just(
                                ApiResponseModel.builder()
                                        .status(CONFLICT)
                                        .code("Failure")
                                        .description(String.format("Failure while add new article title: %s", article.getTitle()))
                                        .build())
                );
    }


    /**
     * Method for RestAPI edit existing article
     *
     * @param article article from user request;
     * @return {@link ApiResponseModel} with edited article
     */
    public Mono<ApiResponseModel> editArticle(ArticleRequestModel article) {
        return articleRepository.save(modelMapper.toArticle(article))
                .flatMap(it -> {
                    return Mono.just(
                            ApiResponseModel.builder()
                                    .status(OK)
                                    .code("Success")
                                    .description(String.format("Successfully edit article, with ID: %s", it.getId()))
                                    .payload(List.of(modelMapper.toArticleResponseModel(it)))
                                    .build());
                })
                .switchIfEmpty(
                        Mono.just(
                                ApiResponseModel.builder()
                                        .status(CONFLICT)
                                        .code("Failure")
                                        .description(String.format("Failure edit article, with ID: %s", article.getId()))
                                        .build())
                );
    }


    /**
     * Method for RestAPI which delete article by ID
     *
     * @param articleId from user request
     * @return {@link ApiResponseModel} with status of operation
     */
    public Mono<ApiResponseModel> remove(UUID articleId) {
        articleRepository.deleteById(articleId)
                .doOnError(e -> {
                    log.warn("{}_error, can`t remove article with ID: {}", getClass().getSimpleName(), articleId);
                });
        return Mono.just(
                ApiResponseModel.builder()
                        .status(OK)
                        .code("Success")
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
                .flatMap(it -> {
                            return Mono.just(
                                    ApiResponseModel.builder()
                                            .status(OK)
                                            .code("Success")
                                            .payload(List.of(modelMapper.toArticleResponseModel(it)))
                                            .build());
                        }
                ).switchIfEmpty(
                        Mono.just(
                                ApiResponseModel.builder()
                                        .status(CONFLICT)
                                        .code("Failure")
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
                                    .code("Success")
                                    .description("Success fetch all articles")
                                    .payload(listOfArticles)
                                    .build());
                })
                .switchIfEmpty(
                        Mono.just(
                                ApiResponseModel.builder()
                                        .status(CONFLICT)
                                        .code("Failure")
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
                                    .code("Success")
                                    .description("Success fetch all articles")
                                    .payload(listOfArticles)
                                    .build());
                })
                .switchIfEmpty(
                        Mono.just(
                                ApiResponseModel.builder()
                                        .status(CONFLICT)
                                        .code("Failure")
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
        return articleRepository.findArticleByLastUpdateIsAfter(LocalDateTime.now()
                        .minusDays(days))
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
        return articleRepository.findAll()
                .doOnError(e -> {
                    log.warn("{}_error, can`t fetch all articles, message {}", getClass().getSimpleName(), e.getMessage());
                    throw new DataAccessException("DataObjectAccessException, can not fetch all articles");
                });
    }
}
