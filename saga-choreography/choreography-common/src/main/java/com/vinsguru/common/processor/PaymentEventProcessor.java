package com.vinsguru.processor;

import com.vinsguru.events.DomainEvent;
import com.vinsguru.events.payment.PaymentEvent;
import reactor.core.publisher.Mono;

public interface PaymentEventProcessor<R extends DomainEvent> extends EventProcessor<PaymentEvent, R> {

    @Override
    default Mono<R> process(PaymentEvent event) {
        if (event instanceof PaymentEvent.PaymentDeducted e) {
            return handle(e);
        } else if (event instanceof PaymentEvent.PaymentDeclined e) {
            return handle(e);
        } else if (event instanceof PaymentEvent.PaymentRefunded e) {
            return handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + event.getClass()));
        }
    }

    Mono<R> handle(PaymentEvent.PaymentDeducted event);

    Mono<R> handle(PaymentEvent.PaymentDeclined event);

    Mono<R> handle(PaymentEvent.PaymentRefunded event);

}