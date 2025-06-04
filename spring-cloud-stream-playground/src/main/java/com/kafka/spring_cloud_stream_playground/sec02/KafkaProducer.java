package com.kafka.spring_cloud_stream_playground.sec02;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binder.reactorkafka.SenderOptionsCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);


//    @Bean
//    public SenderOptionsCustomizer customizer(){
//        return (s, so) -> so.producerProperty(ProducerConfig.ACKS_CONFIG, "all")
//                .producerProperty(ProducerConfig.BATCH_SIZE_CONFIG, "20001");
//    }

    @Bean
    public Supplier<Flux<String>> producer() {
        return () -> Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .map(i -> "msg " + i)
                .doOnNext(m -> log.info("produce: {}",m));
    }


}
