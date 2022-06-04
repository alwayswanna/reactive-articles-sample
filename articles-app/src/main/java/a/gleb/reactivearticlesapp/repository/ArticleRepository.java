package a.gleb.reactivearticlesapp.repository;

import a.gleb.articlecommon.models.db.Article;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ArticleRepository extends ReactiveMongoRepository<Article, UUID> {
    Flux<Article> findArticleByLastUpdateIsAfter(LocalDateTime lastUpdate);
}
