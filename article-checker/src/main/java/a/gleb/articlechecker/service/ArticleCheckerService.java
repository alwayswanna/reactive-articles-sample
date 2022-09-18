package a.gleb.articlechecker.service;

import a.gleb.articlechecker.configuration.properties.ArticleCheckerConfigurationProperties;
import a.gleb.articlechecker.exception.ArticleCheckException;
import a.gleb.articlecommon.models.mq.MqCheckRequest;
import a.gleb.articlecommon.models.mq.MqCheckResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static a.gleb.articlecommon.models.mq.MqStatusCheck.FAILURE;
import static a.gleb.articlecommon.models.mq.MqStatusCheck.SUCCESS;

@Service
@Slf4j
@AllArgsConstructor
public class ArticleCheckerService {

    private final ArticleCheckerConfigurationProperties configurationProperties;
    private final Sinks.Many<MqCheckResponse> publisherToArticleStatusQueue;

    /**
     * Listener check contains or not article words from dictionary-black list;
     *
     * @param mqCheckRequest message from rabbit
     */
    public Mono<Void> receiveMessage(MqCheckRequest mqCheckRequest) {
        log.info("{}_info, start check article with ID: {}", getClass().getSimpleName(), mqCheckRequest.getMessageId());
        return Mono.just(publisherToArticleStatusQueue.tryEmitNext(checkArticle(mqCheckRequest)))
                .doOnError(it -> {
                    log.info("ERROR WHILE PROCEED CHECK: {}", it.getMessage());
                    Mono.error(new ArticleCheckException(it.getMessage()));
                })
                .then();
    }


    private MqCheckResponse checkArticle(MqCheckRequest mqCheckRequest) {
        var mqResponse = new MqCheckResponse();
        mqResponse.setMessageId(mqCheckRequest.getMessageId());

        configurationProperties.getDictionaryBlackListWords()
                .stream()
                .parallel()
                .map(String::toLowerCase)
                .forEach(exclusion -> {
                    if (mqCheckRequest.getTitle().toLowerCase().contains(exclusion) ||
                            mqCheckRequest.getPayload().toLowerCase().contains(exclusion) ||
                            mqCheckRequest.getDescription().toLowerCase().contains(exclusion)) {
                        mqResponse.setStatus(FAILURE);
                    } else {
                        mqResponse.setStatus(SUCCESS);
                    }
                });

        return mqResponse;
    }
}
