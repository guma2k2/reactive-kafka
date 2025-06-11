package com.vinsguru.inventory.messaging.processor;

import com.vinsguru.common.messages.inventory.InventoryRequest;
import com.vinsguru.common.messages.inventory.InventoryResponse;
import com.vinsguru.common.messages.payment.PaymentRequest;
import com.vinsguru.common.processor.RequestProcessor;
import reactor.core.publisher.Mono;

public interface InventoryRequestProcessor extends RequestProcessor<InventoryRequest, InventoryResponse> {

    @Override
    default Mono<InventoryResponse> process(InventoryRequest request) {

        if (request instanceof InventoryRequest.Deduct e) {
            return handle(e);
        } else if (request instanceof InventoryRequest.Restore e) {
            return handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + request.getClass()));
        }
    }

    Mono<InventoryResponse> handle(InventoryRequest.Deduct request);

    Mono<InventoryResponse> handle(InventoryRequest.Restore request);

}