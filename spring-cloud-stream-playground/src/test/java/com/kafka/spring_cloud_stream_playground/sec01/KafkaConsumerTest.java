package com.kafka.spring_cloud_stream_playground.sec01;

import com.kafka.spring_cloud_stream_playground.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Duration;
import java.util.function.Supplier;

@TestPropertySource(properties = {
        "sec=sec01",
        "spring.cloud.function.definition=consumer;testProducer",
        "spring.cloud.stream.bindings.testProducer-out-0.destination=input-topic"
})
@ExtendWith(OutputCaptureExtension.class)
public class KafkaConsumerTest extends AbstractIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerTest.class);

    @Test
    public void consumerTest(CapturedOutput output){
        Mono.delay(Duration.ofMillis(500))
                .then(Mono.fromSupplier(output::getOut))
                .as(StepVerifier::create)
                .consumeNextWith(s -> {
                    log.info(s + "");
                    Assertions.assertTrue(s.contains("consumer received hello world"));
                })
                .verifyComplete();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public Supplier<Flux<String>> testProducer(){
            return () -> Flux.just("hello world");
        }

    }


}