package a.gleb.reactivearticlesapp.controller;


import a.gleb.articlecommon.models.rest.ArticleCreateRequestModel;
import a.gleb.reactivearticlesapp.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static a.gleb.reactivearticlesapp.util.ArticleUtility.articleCreateRequestModel;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class ValidationControllerTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Attempt to create article without 'title'")
    void incorrectRequestTitle() {
        var article = articleCreateRequestModel();
        article.setTitle(null);
        webTestClient.post()
                .uri("/api/v1/create")
                .accept(APPLICATION_JSON)
                .body(Mono.just(article), ArticleCreateRequestModel.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("Attempt to create article without 'description'")
    void incorrectRequestDescription() {
        var article = articleCreateRequestModel();
        article.setDescription(null);
        webTestClient.post()
                .uri("/api/v1/create")
                .accept(APPLICATION_JSON)
                .body(Mono.just(article), ArticleCreateRequestModel.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("Attempt to create article without 'payload'")
    void incorrectRequestPayload() {
        var article = articleCreateRequestModel();
        article.setBody(null);
        webTestClient.post()
                .uri("/api/v1/create")
                .accept(APPLICATION_JSON)
                .body(Mono.just(article), ArticleCreateRequestModel.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
