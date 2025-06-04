package com.kafka.spring_cloud_stream_playground.sec03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Supplier;

@Configuration
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    @Bean
    public Supplier<Flux<String>> producer() {
        return () -> Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .map(i -> "msg " + i)
                .doOnNext(m -> log.info("produce: {}",m));
    }


}
