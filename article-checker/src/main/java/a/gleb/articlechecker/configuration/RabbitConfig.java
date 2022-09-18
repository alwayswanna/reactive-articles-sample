package a.gleb.articlechecker.configuration;

import a.gleb.articlechecker.exception.ArticleCheckException;
import a.gleb.articlechecker.service.ArticleCheckerService;
import a.gleb.articlecommon.models.mq.MqCheckRequest;
import a.gleb.articlecommon.models.mq.MqCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.function.context.PollableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class RabbitConfig {

    @PollableBean
    Supplier<Flux<MqCheckResponse>> articleStatus(Sinks.Many<MqCheckResponse> sinkManyBuffer) {
        return sinkManyBuffer::asFlux;
    }

    @Bean
    Function<Flux<MqCheckRequest>, Mono<Void>> articleCheck(ArticleCheckerService articleCheckerService) {
        return sink -> sink
                .log()
                .onErrorContinue((throwable, o) -> {
                    log.warn("ERROR WHILE RECEIVE MESSAGE: {}", throwable.getMessage());
                    Mono.error(new ArticleCheckException(throwable.getMessage()));
                })
                .map(articleCheckerService::receiveMessage)
                .then();
    }

    @Bean
    Sinks.Many<MqCheckResponse> publisherBuffer() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
