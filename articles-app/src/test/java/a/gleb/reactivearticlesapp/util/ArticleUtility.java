package a.gleb.reactivearticlesapp.util;

import a.gleb.articlecommon.models.mq.MqCheckResponse;
import a.gleb.articlecommon.models.mq.MqStatusCheck;
import a.gleb.articlecommon.models.rest.ArticleCreateRequestModel;
import a.gleb.articlecommon.models.rest.ArticleRequestModel;

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

    public static MqCheckResponse buildResponseRabbit(UUID id, MqStatusCheck status){
        var response =  new MqCheckResponse();
        response.setMessageId(id);
        response.setStatus(status);
        return response;
    }
}
