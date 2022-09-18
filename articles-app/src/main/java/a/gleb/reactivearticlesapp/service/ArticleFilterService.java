package a.gleb.reactivearticlesapp.service;

import a.gleb.articlecommon.models.mq.MqCheckResponse;
import a.gleb.articlecommon.models.rest.ApiResponseModel;
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
    public Mono<ApiResponseModel> filter(@Valid MqCheckResponse mqCheckResponse) {
        log.info("{}_info, receive message with ID: {}", getClass().getSimpleName(), mqCheckResponse.getMessageId());
        return mqCheckResponse.getStatus() == FAILURE ?
                articleService.remove(mqCheckResponse.getMessageId()).doOnError(it ->
                        log.info("Error while remove article with ID: {}, message: {}",
                                mqCheckResponse.getMessageId(),
                                it.getMessage())) : Mono.empty();
    }
}
