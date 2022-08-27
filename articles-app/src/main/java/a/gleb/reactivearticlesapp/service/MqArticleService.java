package a.gleb.reactivearticlesapp.service;

import a.gleb.articlecommon.models.mq.MqCheckResponse;
import a.gleb.articlecommon.models.mq.MqStatusCheck;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@Slf4j
@AllArgsConstructor
public class MqArticleService {

    private final ArticleService articleService;


    /**
     * Remove article from database if {@link MqCheckResponse} status is FAILURE
     * @param message from article checker service
     */
    @RabbitListener(queues = "article.article-status")
    public void receivedMessage(@Valid MqCheckResponse message) {
        log.info("{}_info, result of check: msg: {}", getClass().getSimpleName(), message.toString());
        if (message.getStatus() == MqStatusCheck.FAILURE) {
            log.info("{}_info, clean up articles with bad quality content: {}", getClass().getSimpleName(), message.getMessageId());
            articleService.remove(message.getMessageId())
                    .doOnSuccess(msg -> {
                        log.info("{}_info, success deleting article with bad content, id: {}",
                                getClass().getSimpleName(), message.getMessageId());
                    })
                    .doOnError(e -> {
                        log.warn("{}_error, while deleting article with bad content, id: {}, msg: {}",
                                getClass().getSimpleName(),
                                message.getMessageId(),
                                e.getMessage());
                    })
                    .subscribe();
        }
    }
}
