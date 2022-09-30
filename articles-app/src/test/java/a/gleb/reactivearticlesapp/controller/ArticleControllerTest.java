package a.gleb.reactivearticlesapp.controller;


import a.gleb.articlecommon.models.rest.ApiResponseModel;
import a.gleb.articlecommon.models.rest.ArticleCreateRequestModel;
import a.gleb.reactivearticlesapp.BaseIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static a.gleb.reactivearticlesapp.util.ArticleUtility.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class ArticleControllerTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Success create article.")
    void createArticleTest() {
        webTestClient.post()
                .uri("/api/v1/create")
                .accept(APPLICATION_JSON)
                .body(Mono.just(articleCreateRequestModel()), ArticleCreateRequestModel.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @DisplayName("Success edit article.")
    void editExistingArticle() {
        var response = webTestClient.post()
                .uri("/api/v1/create")
                .accept(APPLICATION_JSON)
                .body(Mono.just(articleCreateRequestModel()), ArticleCreateRequestModel.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ApiResponseModel.class);

        var actual = response.getResponseBody().blockFirst();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(SUCCESS_CODE, actual.getCode());
        var id = actual.getPayload().get(0).getId();

        var editResponse = webTestClient.put()
                .uri("/api/v1/edit")
                .accept(APPLICATION_JSON)
                .body(Mono.just(articleRequestModel(id)), ArticleCreateRequestModel.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ApiResponseModel.class);
        var editActualResponse = editResponse.getResponseBody().blockFirst();
        Assertions.assertNotNull(editActualResponse);
        Assertions.assertEquals(SUCCESS_CODE, editActualResponse.getCode());
    }

    @Test
    @DisplayName("Success find article by ID")
    void fetchArticleById() {
        var response = webTestClient.post()
                .uri("/api/v1/create")
                .accept(APPLICATION_JSON)
                .body(Mono.just(articleCreateRequestModel()), ArticleCreateRequestModel.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ApiResponseModel.class);

        var actual = response.getResponseBody().blockFirst();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(SUCCESS_CODE, actual.getCode());
        var id = actual.getPayload().get(0).getId();

        var byIdResponse = webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/api/v1/get-by-id")
                                .queryParam("articleId", id)
                                .build())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ApiResponseModel.class);

        var actualById = byIdResponse.getResponseBody().blockFirst();
        Assertions.assertNotNull(actualById);
        Assertions.assertEquals(SUCCESS_CODE, actualById.getCode());
    }

    @Test
    @DisplayName("Success get all articles")
    void fetchAllArticles() {
        var response = webTestClient.get()
                .uri("/api/v1/all")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ApiResponseModel.class);
        var actual = response.getResponseBody().blockFirst();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(SUCCESS_CODE, actual.getCode());
    }

    @Test
    @DisplayName("Success fetch article by last week")
    void fetchArticlesByLastWeek() {
        var response = webTestClient.get()
                .uri("/api/v1/weekly")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ApiResponseModel.class);
        var actual = response.getResponseBody().blockFirst();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(SUCCESS_CODE, actual.getCode());
    }

    @Test
    @DisplayName("Success remove article")
    void removeExistingArticle() {
        var response = webTestClient.post()
                .uri("/api/v1/create")
                .accept(APPLICATION_JSON)
                .body(Mono.just(articleCreateRequestModel()), ArticleCreateRequestModel.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ApiResponseModel.class);

        var actual = response.getResponseBody().blockFirst();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(SUCCESS_CODE, actual.getCode());
        var id = actual.getPayload().get(0).getId();

        var deleteResponse = webTestClient.delete()
                .uri("/api/v1/remove/" + id.toString())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ApiResponseModel.class);
        var deleteActualResponse = deleteResponse.getResponseBody().blockFirst();
        Assertions.assertNotNull(deleteActualResponse);
        Assertions.assertEquals(SUCCESS_CODE, actual.getCode());
    }
}
