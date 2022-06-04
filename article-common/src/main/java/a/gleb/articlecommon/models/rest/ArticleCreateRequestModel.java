package a.gleb.articlecommon.models.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Schema(description = "Model for create articles")
@Builder
@Getter
@Setter
@Validated
public class ArticleCreateRequestModel implements Serializable {

    @Schema(description = "Title of article", required = true)
    @NotNull
    private String title;

    @Schema(description = "Description of article", required = true)
    @NotNull
    private String description;

    @Schema(description = "Payload of article", required = true)
    @NotNull
    private String body;
}
