package a.gleb.articlecommon.models.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response model with article in payload")
@Validated
@Builder
@Getter
@Setter
public class ArticleResponseModel {

    @Schema(description = "Time of response", required = true)
    @NotNull
    Timestamp timestamp;

    @Schema(description = "Unique ID of article", required = true)
    @NotNull
    private UUID id;

    @Schema(description = "Title of article", required = true)
    @NotNull
    private String title;

    @Schema(description = "Description of article")
    private String description;

    @Schema(description = "Payload of article")
    private String body;

    @Schema(description = "Date of create/Date of last update", required = true)
    @DateTimeFormat
    @NotNull
    private LocalDateTime lastUpdate;

}
