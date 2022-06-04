package a.gleb.articlecommon.models.mq;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MqCheckResponse {

    @NotNull
    private UUID messageId;

    @NotNull
    private MqStatusCheck status;
}
