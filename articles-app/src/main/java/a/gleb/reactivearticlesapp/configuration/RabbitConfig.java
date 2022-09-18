package a.gleb.reactivearticlesapp.configuration;


import a.gleb.articlecommon.models.mq.AuthorChangeInfoEvent;
import a.gleb.articlecommon.models.mq.MqCheckResponse;
import a.gleb.reactivearticlesapp.service.ArticleFilterService;
import a.gleb.reactivearticlesapp.service.ArticleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Function;

import static a.gleb.articlecommon.models.mq.EventType.EDIT;

@Configuration
@AllArgsConstructor
@Slf4j
public class RabbitConfig {

    private final ArticleFilterService articleFilterService;
    private final ArticleService articleService;

    @Bean
    Function<Flux<MqCheckResponse>, Flux<Object>> articleStatus() {
        return sink -> sink.log()
                .flatMap(articleFilterService::filter);
    }

    @Bean
    Function<Flux<AuthorChangeInfoEvent>, Flux<Object>> notifyEvent() {
        return sink -> sink.log()
                .flatMap(event ->
                        event.getEventType().equals(EDIT) ? articleService.changeArticleInfo(event) :
                                articleService.removeRelatedArticles(event)
                );
    }
}
