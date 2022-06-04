package a.gleb.articlecommon.models.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@Validated
@Schema(description = "Object of API response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseModel {

    @Schema(required = true, description = "Response status code")
    @NotNull
    private HttpStatus status;

    @Schema(required = true, description = "Response code")
    @NotNull
    private String code;

    @Schema(required = true, description = "Description of response")
    @NotNull
    private String description;

    @Schema(required = false, description = "Additional attributes of response")
    private Map<String, String> attributes;

    @Schema(required = false, description = "Article, if status is 200")
    private List<ArticleResponseModel> payload;

}
