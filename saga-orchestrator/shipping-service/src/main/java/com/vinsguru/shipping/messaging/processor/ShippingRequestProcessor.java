package com.vinsguru.shipping.messaging.processor;

import com.vinsguru.common.messages.inventory.InventoryRequest;
import com.vinsguru.common.messages.shipping.ShippingRequest;
import com.vinsguru.common.messages.shipping.ShippingResponse;
import com.vinsguru.common.processor.RequestProcessor;
import reactor.core.publisher.Mono;

public interface ShippingRequestProcessor extends RequestProcessor<ShippingRequest, ShippingResponse> {

    @Override
    default Mono<ShippingResponse> process(ShippingRequest request) {
        if (request instanceof ShippingRequest.Schedule e) {
            return handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + request.getClass()));
        }
    }

    Mono<ShippingResponse> handle(ShippingRequest.Schedule request);

}