package a.gleb.reactivearticlesapp.db.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Article {

    private UUID id;
    private UUID authorId;
    private String title;
    private String description;
    private String body;
    private LocalDateTime lastUpdate;
    private String authorLogin;
}
