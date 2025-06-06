package com.vinsguru.processor;

import com.vinsguru.events.DomainEvent;
import com.vinsguru.events.shipping.ShippingEvent;
import reactor.core.publisher.Mono;

public interface ShippingEventProcessor<R extends DomainEvent> extends EventProcessor<ShippingEvent, R> {

    /*
        To follow the same pattern as other event processors.
        also for type!
     */

    @Override
    default Mono<R> process(ShippingEvent event) {
        if (event instanceof ShippingEvent.ShippingScheduled e) {
            return handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + event.getClass()));
        }
    }

    Mono<R> handle(ShippingEvent.ShippingScheduled event);

}