package com.vinsguru.payment.messaging.config;


import com.vinsguru.common.events.order.OrderEvent;
import com.vinsguru.common.events.payment.PaymentEvent;
import com.vinsguru.common.processor.OrderEventProcessor;
import com.vinsguru.common.util.MessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Configuration
public class OrderEventProcessorConfig {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProcessorConfig.class);
    private final OrderEventProcessor<PaymentEvent> eventProcessor;

    public OrderEventProcessorConfig(OrderEventProcessor<PaymentEvent> eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @Bean
    public Function<Flux<Message<OrderEvent>>, Flux<Message<PaymentEvent>>> processor() {
        return flux -> flux.map(MessageConverter::toRecord)
                .doOnNext(orderEventRecord -> log.info("customer payment received {}", orderEventRecord.message()))
                .concatMap(r -> eventProcessor.process(r.message())
                        .doOnSuccess(paymentEvent -> r.acknowledgement().acknowledge()))
                .map(this::toMessage);
    }

    private Message<PaymentEvent> toMessage(PaymentEvent paymentEvent) {
        return MessageBuilder.withPayload(paymentEvent)
                .setHeader(KafkaHeaders.KEY, paymentEvent.orderId().toString())
                .build();
    }
}
