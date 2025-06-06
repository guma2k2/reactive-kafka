package com.vinsguru.events.order;

import com.vinsguru.events.DomainEvent;
import com.vinsguru.events.OrderSaga;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public sealed interface OrderEvent extends DomainEvent, OrderSaga {

    /*
        Intentionally using primitive wrapper types to keep things simple
     */

    @Builder
    record OrderCreated(UUID orderId,
                        Integer productId,
                        Integer customerId,
                        Integer quantity,
                        Integer unitPrice,
                        Integer totalAmount,
                        Instant createdAt) implements OrderEvent {
    }

    @Builder
    record OrderCancelled(UUID orderId,
                          Instant createdAt) implements OrderEvent {
    }

    @Builder
    record OrderCompleted(UUID orderId,
                          Instant createdAt) implements OrderEvent {
    }

}