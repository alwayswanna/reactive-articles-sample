package a.gleb.reactivearticlesapp;

import a.gleb.reactivearticlesapp.security.WithMockedUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@WithMockedUser
public abstract class BaseIntegrationTest {

    @Container
    public static MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest");
    @Container
    public static RabbitMQContainer RABBIT_MQ_CONTAINER = new RabbitMQContainer("rabbitmq:latest");


    static {
        MONGO_DB_CONTAINER.start();
        RABBIT_MQ_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
        registry.add("spring.data.mongodb.port", MONGO_DB_CONTAINER::getFirstMappedPort);
        registry.add("spring.rabbitmq.host", RABBIT_MQ_CONTAINER::getHost);
        registry.add("spring.rabbitmq.port", RABBIT_MQ_CONTAINER::getFirstMappedPort);
    }

    @AfterAll
    static void shutdown() {
        MONGO_DB_CONTAINER.stop();
    }
}
