package a.gleb.reactivearticlesapp.job;

import a.gleb.articlecommon.models.db.Article;
import a.gleb.reactivearticlesapp.configuration.properties.ArticleApplicationProperties;
import a.gleb.reactivearticlesapp.mapper.ModelMapper;
import a.gleb.reactivearticlesapp.service.ArticleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CheckArticleContentTask {

    private static final String OUT_MESSAGE_CHANNEL = "article-check-out-0";

    private final ArticleService articleService;
    private final StreamBridge streamBridge;
    private final ArticleApplicationProperties properties;
    private final ModelMapper modelMapper;

    /**
     * Cron job which start every day and sent part of {@link Article} to verification
     */
    @Scheduled(cron = "${reactive.scheduler.article-check-daily}")
    public void processCheckArticleByLastDay() {
        var onCheck = articleService.findArticlesIsAfter(properties.getCheckArticlesIntervalDays())
                .map(modelMapper::toMqCheckRequest)
                .toStream()
                .toList();
        onCheck.forEach(it -> {
            log.info("{}_info, send article to check, id: {}", getClass().getSimpleName(), it.getMessageId());
            streamBridge.send(OUT_MESSAGE_CHANNEL, it);
        });
    }

    /**
     * Cron job which start every month and sent all {@link Article} to verification
     */
    @Scheduled(cron = "${reactive.scheduler.article-check-monthly}")
    public void processCheckArticleByLastMonth() {
        var onCheck = articleService.findAllArticles()
                .map(modelMapper::toMqCheckRequest)
                .toStream()
                .toList();
        onCheck.forEach(it -> {
            log.info("{}_info, send article to check, id: {}", getClass().getSimpleName(), it.getMessageId());
            streamBridge.send(OUT_MESSAGE_CHANNEL, it);
        });
    }

}
