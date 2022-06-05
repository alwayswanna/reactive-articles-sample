package a.gleb.reactivearticlesapp;

import a.gleb.articlecommon.models.rest.ArticleCreateRequestModel;
import a.gleb.articlecommon.models.rest.ArticleRequestModel;
import lombok.experimental.UtilityClass;

import java.util.UUID;

public class ArticleUtility {

    public static String SUCCESS_CODE = "Success";

    public static ArticleCreateRequestModel articleCreateRequestModel() {
        return ArticleCreateRequestModel.builder()
                .title("First title")
                .description("Description")
                .body("Payload of article")
                .build();
    }

    public static ArticleRequestModel articleRequestModel(UUID id) {
        return ArticleRequestModel.builder()
                .id(id)
                .title("Change title")
                .description("Change description")
                .body("Change payload")
                .build();
    }
}
