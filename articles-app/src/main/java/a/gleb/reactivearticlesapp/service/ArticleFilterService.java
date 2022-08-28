package a.gleb.reactivearticlesapp.service;

import a.gleb.articlecommon.models.mq.MqCheckResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static a.gleb.articlecommon.models.mq.MqStatusCheck.FAILURE;

@Service
@Slf4j
@AllArgsConstructor
public class ArticleFilterService {

    private final ArticleService articleService;


    /**
     * Remove article from database if {@link MqCheckResponse} status is FAILURE
     *
     * @param mqCheckResponse from article checker service
     */
    public Mono<Void> filter(@Valid MqCheckResponse mqCheckResponse) {
        log.info("{}_info, receive message with ID: {}", getClass().getSimpleName(), mqCheckResponse.getMessageId());
        if (FAILURE == mqCheckResponse.getStatus()) {
            log.info("{}_info, clean up article with bad content, {}",
                    getClass().getSimpleName(),
                    mqCheckResponse.getMessageId()
            );
            articleService.remove(mqCheckResponse.getMessageId())
                    .doOnError(it -> log.info("Error while remove article with ID: {}, message: {}",
                            mqCheckResponse.getMessageId(),
                            it.getMessage()
                    ));
        }
        return Mono.empty().then();
    }
}
