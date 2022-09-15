package a.gleb.articlecommon.models.mq;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MqCheckRequest implements Serializable {

    @NotNull
    private UUID messageId;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String payload;
}
