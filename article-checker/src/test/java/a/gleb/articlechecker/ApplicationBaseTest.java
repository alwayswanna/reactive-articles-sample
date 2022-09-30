package a.gleb.articlechecker;


import a.gleb.articlecommon.models.mq.MqCheckRequest;
import a.gleb.articlecommon.models.mq.MqCheckResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;

import java.util.UUID;

import static a.gleb.articlecommon.models.mq.MqStatusCheck.FAILURE;
import static a.gleb.articlecommon.models.mq.MqStatusCheck.SUCCESS;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public abstract class ApplicationBaseTest {

    private static final String TITLE_DEFAULT = "Default title";
    private static final String DESCRIPTION_DEFAULT = "Default description";
    private static final String PAYLOAD_DEFAULT = "Default payload";

    private static final GenericContainer<?> rabbitMqContainer = new RabbitMQContainer
            ("rabbitmq:latest")
            .withExposedPorts(5672);

    static {
        rabbitMqContainer.start();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMqContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMqContainer::getFirstMappedPort);
    }

    /**
     * Build model for tests.
     *
     * @param id          UUID or random;
     * @param title       String or {@link ApplicationBaseTest#TITLE_DEFAULT}
     * @param payload     String or {@link ApplicationBaseTest#PAYLOAD_DEFAULT}
     * @param description String or {@link ApplicationBaseTest#DESCRIPTION_DEFAULT}
     * @return {@link MqCheckRequest}
     */
    public MqCheckRequest buildRequest(
            UUID id,
            String title,
            String payload,
            String description
    ) {
        var mqRequest = new MqCheckRequest();
        mqRequest.setMessageId(id == null ? UUID.randomUUID() : id);
        mqRequest.setPayload(payload == null ? PAYLOAD_DEFAULT : payload);
        mqRequest.setTitle(title == null ? TITLE_DEFAULT : title);
        mqRequest.setDescription(description == null ? DESCRIPTION_DEFAULT : description);
        return mqRequest;
    }

    public MqCheckResponse buildSuccess(UUID messageId){
        var mqResponse = new MqCheckResponse();
        mqResponse.setMessageId(messageId);
        mqResponse.setStatus(SUCCESS);
        return mqResponse;
    }

    public MqCheckResponse buildFailure(UUID messageId){
        var mqResponse = new MqCheckResponse();
        mqResponse.setMessageId(messageId);
        mqResponse.setStatus(FAILURE);
        return mqResponse;
    }

    @AfterAll
    static void tearDown() {
        rabbitMqContainer.stop();
    }
}
