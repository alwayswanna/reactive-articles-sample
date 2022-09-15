package a.gleb.articlecommon.models.mq;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@Validated
public class AuthorChangeInfoEvent {

    @NotNull
    private LocalDateTime timestamp;

    @NotNull
    private EventType eventType;

    @NotNull
    private UUID accountId;

    private String newUsername;
}
