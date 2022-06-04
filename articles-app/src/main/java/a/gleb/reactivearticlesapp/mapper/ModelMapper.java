package a.gleb.reactivearticlesapp.mapper;

import a.gleb.articlecommon.models.mq.MqCheckRequest;
import a.gleb.articlecommon.models.db.Article;
import a.gleb.articlecommon.models.rest.ArticleCreateRequestModel;
import a.gleb.articlecommon.models.rest.ArticleRequestModel;
import a.gleb.articlecommon.models.rest.ArticleResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ModelMapper {

    @Mapping(target = "author_id", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(localDataTime())")
    Article toArticle(ArticleRequestModel articleRequestModel);

    @Mapping(target = "author_id", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(localDataTime())")
    @Mapping(target = "id", expression = "java(generateRandomUUID())")
    Article toNewArticle(ArticleCreateRequestModel articleCreateRequestModel);

    @Mapping(target = "timestamp", expression = "java(timestamp())")
    ArticleResponseModel toArticleResponseModel(Article article);

    @Mapping(source = "id", target = "messageId")
    @Mapping(source = "body", target = "payload")
    MqCheckRequest toMqCheckRequest(Article article);


    default LocalDateTime localDataTime() {
        return LocalDateTime.now();
    }

    default Timestamp timestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    default UUID generateRandomUUID(){return UUID.randomUUID();}
}
