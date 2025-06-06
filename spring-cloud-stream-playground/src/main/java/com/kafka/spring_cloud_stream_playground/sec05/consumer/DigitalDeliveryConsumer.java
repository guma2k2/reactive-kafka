package com.kafka.spring_cloud_stream_playground.sec05.consumer;
import com.kafka.spring_cloud_stream_playground.common.MessageConverter;
import com.kafka.spring_cloud_stream_playground.common.Record;
import com.kafka.spring_cloud_stream_playground.sec05.dto.DigitalDelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class DigitalDeliveryConsumer {

    private static final Logger log = LoggerFactory.getLogger(DigitalDeliveryConsumer.class);

    @Bean
    public Function<Flux<Message<DigitalDelivery>>, Mono<Void>> digitalDelivery() {
        return flux -> flux
                .map(MessageConverter::toRecord)
                .doOnNext(this::printDetails)
                .then();
    }

    private void printDetails(Record<DigitalDelivery> record) {
        log.info("digital consumer {}", record.message());
        record.acknowledgement().acknowledge();
    }

}