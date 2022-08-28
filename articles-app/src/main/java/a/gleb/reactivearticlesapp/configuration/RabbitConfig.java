package a.gleb.reactivearticlesapp.configuration;


import a.gleb.articlecommon.models.mq.AuthorChangeInfoEvent;
import a.gleb.articlecommon.models.mq.EventType;
import a.gleb.articlecommon.models.mq.MqCheckResponse;
import a.gleb.reactivearticlesapp.service.ArticleFilterService;
import a.gleb.reactivearticlesapp.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@AllArgsConstructor
public class RabbitConfig {

    private final ArticleFilterService articleFilterService;
    private final ArticleService articleService;

    @Bean
    Function<Flux<MqCheckResponse>, Mono<Void>> articleStatus() {
        return sink -> (Mono<Void>) sink.log()
                .map(articleFilterService::filter)
                .then();
    }

    @Bean
    Function<Flux<AuthorChangeInfoEvent>, Mono<Void>> notifyEvent() {
        return sink -> (Mono<Void>) sink.log()
                .map(event ->
                        event.getEventType() == EventType.EDIT ? articleService.changeArticleInfo(event) :
                                articleService.removeRelatedArticles(event)
                )
                .then();
    }
}
