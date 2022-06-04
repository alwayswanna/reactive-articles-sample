package a.gleb.articlechecker.service;

import a.gleb.articlechecker.configuration.properties.ArticleCheckerConfigurationProperties;
import a.gleb.articlecommon.models.mq.MqCheckRequest;
import a.gleb.articlecommon.models.mq.MqCheckResponse;
import a.gleb.articlecommon.models.mq.MqStatusCheck;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

import static java.util.Locale.ROOT;

@Service
@Slf4j
@AllArgsConstructor
public class ArticleCheckerService {

    private static final String ARTICLE_STATUS_OUT = "article-status-out-0";

    private final ArticleCheckerConfigurationProperties configurationProperties;
    private final StreamBridge streamBridge;


    /**
     * Listener check contains or not article words from dictionary-black list;
     *
     * @param mqCheckRequest message from rabbit
     */
    @RabbitListener(queues = "article.article-check")
    public void receiveMessage(@Valid MqCheckRequest mqCheckRequest) {
        log.info("{}_info, start check article with ID: {}", getClass().getSimpleName(), mqCheckRequest.getMessageId());
        var result = new MqCheckResponse();
        result.setMessageId(mqCheckRequest.getMessageId());

        configurationProperties.getDictionaryBlackListWords()
                .stream()
                .map(it -> it.toLowerCase(ROOT))
                .forEach(it -> {
                    if (mqCheckRequest.getDescription().toLowerCase(ROOT).contains(it) ||
                            mqCheckRequest.getTitle().toLowerCase(ROOT).contains(it) ||
                            mqCheckRequest.getPayload().toLowerCase(ROOT).contains(it)) {
                        log.info("{}_info, article with ID: {}, contains black list word: {}",
                                getClass().getSimpleName(), mqCheckRequest.getMessageId(), it);
                        result.setStatus(MqStatusCheck.FAILURE);
                    } else {
                        log.info("{}_info, article with ID: {}, success check", getClass().getSimpleName(), mqCheckRequest.getMessageId());
                        result.setStatus(MqStatusCheck.SUCCESS);
                    }
                    streamBridge.send(ARTICLE_STATUS_OUT, result);
                    log.info("{}_info, end check article with ID: {}", getClass().getSimpleName(), mqCheckRequest.getMessageId());
                });
    }
}
