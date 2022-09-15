package a.gleb.reactivearticlesapp.db.repository;

import a.gleb.reactivearticlesapp.db.entity.Article;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ArticleRepository extends ReactiveMongoRepository<Article, UUID> {
    Flux<Article> findArticleByLastUpdateIsAfter(LocalDateTime lastUpdate);

    Flux<Article> findAllByAuthorId(UUID authorId);
}
