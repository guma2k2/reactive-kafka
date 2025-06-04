package com.kafka.spring_cloud_stream_playground.sec05.consumer;

import com.kafka.spring_cloud_stream_playground.sec05.dto.OrderEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Configuration
public class OrderEventConsumerConfig {

    Function<Flux<Message<OrderEvent>>> consumer() {
        
    }
}
