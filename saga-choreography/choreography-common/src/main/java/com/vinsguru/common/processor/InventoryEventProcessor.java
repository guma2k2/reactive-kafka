package com.vinsguru.processor;

import com.vinsguru.events.DomainEvent;
import com.vinsguru.events.inventory.InventoryEvent;
import reactor.core.publisher.Mono;

public interface InventoryEventProcessor<R extends DomainEvent> extends EventProcessor<InventoryEvent, R> {

    @Override
    default Mono<R> process(InventoryEvent event) {
        if (event instanceof InventoryEvent.InventoryDeducted e) {
            return handle(e);
        } else if (event instanceof InventoryEvent.InventoryDeclined e) {
            return handle(e);
        } else if (event instanceof InventoryEvent.InventoryRestored e) {
            return handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + event.getClass()));
        }
    }
    Mono<R> handle(InventoryEvent.InventoryDeducted event);

    Mono<R> handle(InventoryEvent.InventoryDeclined event);

    Mono<R> handle(InventoryEvent.InventoryRestored event);

}