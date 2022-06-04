package a.gleb.articlecommon.models.mq;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class MqCheckRequest {

    public MqCheckRequest(){}

    @NotNull
    private UUID messageId;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String payload;
}
