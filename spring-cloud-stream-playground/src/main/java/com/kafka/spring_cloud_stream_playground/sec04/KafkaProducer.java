package com.kafka.spring_cloud_stream_playground.sec04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Supplier;

@Configuration
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    @Bean
    public Supplier<Flux<Message<String>>> producer() {
        return () -> Flux.interval(Duration.ofSeconds(1))
                .take(5)
                .map(this::toMessage)
                .doOnNext(m -> log.info("produced {}", m));
    }

    private Message<String> toMessage(long i) {
        return MessageBuilder.withPayload("msg " + i)
                .setHeader(KafkaHeaders.KEY, ("key-" + i))
                .setHeader("my-dummy-key", ("dummy-value-" + i))
                .build();
    }

}
