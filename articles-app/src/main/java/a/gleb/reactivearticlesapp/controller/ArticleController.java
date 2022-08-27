package a.gleb.reactivearticlesapp.controller;


import a.gleb.articlecommon.models.rest.ApiResponseModel;
import a.gleb.articlecommon.models.rest.ArticleCreateRequestModel;
import a.gleb.articlecommon.models.rest.ArticleRequestModel;
import a.gleb.articlecommon.models.rest.ArticleResponseModel;
import a.gleb.reactivearticlesapp.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static a.gleb.reactivearticlesapp.configuration.OpenApiConfig.NAME_SECURITY_SCHEMA;
import static a.gleb.reactivearticlesapp.controller.ArticleController.ARTICLE_CONTROLLER_TAG;

@Controller
@RequestMapping("/api/v1")
@AllArgsConstructor
@Tag(name = ARTICLE_CONTROLLER_TAG)
public class ArticleController {

    public static final String ARTICLE_CONTROLLER_TAG = "article.management.controller";

    private final ArticleService articleService;

    /**
     * Create new article
     *
     * @param article user data
     * @return {@link ApiResponseModel} with status of operation;
     */
    @Operation(
            summary = "Create new article",
            security = @SecurityRequirement(name = NAME_SECURITY_SCHEMA),
            tags = {ARTICLE_CONTROLLER_TAG}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "UNAUTHORIZED", responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "INTERNAL SERVER ERROR", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    )
            }
    )
    @ResponseBody
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponseModel> createNewArticle(@RequestBody @Valid ArticleCreateRequestModel article) {
        return articleService.save(article);
    }

    /**
     * Edit existing article
     *
     * @param article user data
     * @return {@link ApiResponseModel} with status of operation;
     */
    @Operation(
            summary = "Edit existing article",
            security = @SecurityRequirement(name = NAME_SECURITY_SCHEMA),
            tags = {ARTICLE_CONTROLLER_TAG}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "UNAUTHORIZED", responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "INTERNAL SERVER ERROR", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    )
            }
    )
    @ResponseBody
    @PutMapping(value = "/edit")
    public Mono<ApiResponseModel> editArticle(@RequestBody @Valid ArticleRequestModel article) {
        return articleService.editArticle(article);
    }

    /**
     * Returns article with selected ID
     *
     * @param articleId selected ID
     * @return {@link ArticleResponseModel} by ID
     */
    @Operation(
            summary = "Find article by ID",
            tags = {ARTICLE_CONTROLLER_TAG}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "UNAUTHORIZED", responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "INTERNAL SERVER ERROR", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    )
            }
    )
    @ResponseBody
    @GetMapping(value = "/get-by-id")
    public Mono<ApiResponseModel> getArticleById(@RequestParam @NotNull UUID articleId) {
        return articleService.findById(articleId);
    }

    /**
     * Returns all articles from MongoDB
     *
     * @return all {@link ArticleResponseModel} which exist in DB
     */
    @Operation(
            summary = "Fetch all articles",
            tags = {ARTICLE_CONTROLLER_TAG}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "UNAUTHORIZED", responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "INTERNAL SERVER ERROR", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    )
            }
    )
    @ResponseBody
    @GetMapping(value = "/all")
    public Mono<ApiResponseModel> fetchAll() {
        return articleService.findAll();
    }

    /**
     * Return articles where last-update/create is not before last week
     *
     * @return {@link ArticleResponseModel} list with last editable articles
     */
    @Operation(
            summary = "Fetch last articles",
            tags = {ARTICLE_CONTROLLER_TAG}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "UNAUTHORIZED", responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "INTERNAL SERVER ERROR", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    )
            }
    )
    @ResponseBody
    @GetMapping(value = "/weekly")
    public Mono<ApiResponseModel> fetchArticlesByLastWeek() {
        return articleService.fetchPartOfArticles();
    }

    /**
     * Remove selected article
     *
     * @param id for remove article
     * @return {@link ApiResponseModel} with status of operation
     */
    @Operation(
            summary = "Remove selected article",
            security = @SecurityRequirement(name = NAME_SECURITY_SCHEMA),
            tags = {ARTICLE_CONTROLLER_TAG}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "UNAUTHORIZED", responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "INTERNAL SERVER ERROR", responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponseModel.class))
                    )
            }
    )
    @ResponseBody
    @DeleteMapping(value = "/remove/{id}")
    public Mono<ApiResponseModel> remove(@PathVariable UUID id) {
        return articleService.remove(id);
    }

}
