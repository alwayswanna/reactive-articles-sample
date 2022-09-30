package a.gleb.articlechecker.service;

import a.gleb.articlechecker.ApplicationBaseTest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

@Slf4j
class ArticleServiceRabbitTest extends ApplicationBaseTest {

    @Autowired
    @Qualifier("articleCheck-in-0")
    private MessageChannel input;

    @Autowired
    @Qualifier("articleStatus-out-0")
    private MessageChannel output;

    @Autowired
    private MessageCollector collector;

    private static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("success")
    @DisplayName("Success check.")
    void correctWordsInRequest(String word) {
        var messageId = UUID.randomUUID();
        var messageBody = buildRequest(messageId, word, word, word);

        this.input.send(new GenericMessage<>(messageBody));

        BlockingQueue<Message<?>> messages = this.collector.forChannel(this.output);
        assertThat("Does not match", messages, receivesPayloadThat(is(
                objectMapper.writeValueAsString(buildSuccess(messageId))
        )));
        log.info("Messages: -> {}", messages);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("failure")
    @DisplayName("Failure check.")
    void incorrectWordInRequest(String word) {
        var messageId = UUID.randomUUID();
        var messageBody = buildRequest(messageId, word, word, word);

        this.input.send(new GenericMessage<>(messageBody));

        BlockingQueue<Message<?>> messages = this.collector.forChannel(this.output);
        assertThat("Does not match", messages, receivesPayloadThat(is(
                objectMapper.writeValueAsString(buildFailure(messageId))
        )));
        log.info("Messages: -> {}", messages);
    }

    static Stream<Arguments> success() {
        return Stream.of(
                        "cars", "sports", "ball", "bottle",
                        "commodo", "consequat", "laboris",
                        "laboris nisi ut aliquip ex ea commodo consequat")
                .map(Arguments::of);
    }

    static Stream<Arguments> failure() {
        return Stream.of("virgin", "simp", "incel").map(Arguments::of);
    }
}
