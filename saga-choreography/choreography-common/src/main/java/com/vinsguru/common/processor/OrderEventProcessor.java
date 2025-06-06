package com.vinsguru.common.processor;

import com.vinsguru.common.events.DomainEvent;
import com.vinsguru.common.events.order.OrderEvent;
import reactor.core.publisher.Mono;

public interface OrderEventProcessor <R extends DomainEvent> extends EventProcessor<OrderEvent, R> {
    @Override
    default Mono<R> process(OrderEvent event) {
        if (event instanceof OrderEvent.OrderCreated e) {
            return handle(e);
        } else if (event instanceof OrderEvent.OrderCancelled e) {
            return handle(e);
        } else if (event instanceof OrderEvent.OrderCompleted e) {
            return handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + event.getClass()));
        }
    }
    Mono<R> handle(OrderEvent.OrderCompleted event);

    Mono<R> handle(OrderEvent.OrderCancelled event);

    Mono<R> handle(OrderEvent.OrderCreated event);

}
