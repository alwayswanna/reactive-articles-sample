package a.gleb.reactivearticlesapp.service;

import a.gleb.articlecommon.models.mq.MqStatusCheck;
import a.gleb.reactivearticlesapp.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.UUID;

import static a.gleb.reactivearticlesapp.util.ArticleUtility.buildResponseRabbit;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArticleCheckRabbitTest extends BaseIntegrationTest {

    @MockBean
    private ArticleService articleService;

    @Autowired
    @Qualifier("articleStatus-in-0")
    private MessageChannel input;

    @Test
    @DisplayName("When article-checker returns 'success'")
    void testArticleValidationBindingSuccess() {
        var messageId = UUID.randomUUID();
        var messageBody = buildResponseRabbit(messageId, MqStatusCheck.SUCCESS);

        input.send(new GenericMessage<>(messageBody));

        verify(articleService, never()).remove(any());
    }

    @Test
    @DisplayName("When article-checker returns 'failure'")
    void testArticleValidationBindingFailure() {
        var messageId = UUID.randomUUID();
        var messageBody = buildResponseRabbit(messageId, MqStatusCheck.FAILURE);

        input.send(new GenericMessage<>(messageBody));

        verify(articleService, atLeastOnce()).remove(any());
    }
}
