package a.gleb.reactivearticlesapp;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = MOCK)
@ContextConfiguration(initializers = BaseIntegrationTest.Initializer.class)
public abstract class BaseIntegrationTest {

    @Container
    public static MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest");

    @BeforeAll
    static void setUp() {
        MONGO_DB_CONTAINER.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            MONGO_DB_CONTAINER.start();
            TestPropertyValues.of(
                    String.format("spring.data.mongodb.uri: %s", MONGO_DB_CONTAINER.getReplicaSetUrl())
            ).applyTo(applicationContext);
        }
    }

    @AfterAll
    static void shutdown(){
        MONGO_DB_CONTAINER.stop();
    }

}
