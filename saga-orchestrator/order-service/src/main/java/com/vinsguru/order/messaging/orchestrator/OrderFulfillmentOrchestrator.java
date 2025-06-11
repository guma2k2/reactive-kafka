package com.vinsguru.order.messaging.orchestrator;

import com.vinsguru.common.messages.Request;
import com.vinsguru.common.messages.Response;
import com.vinsguru.common.messages.inventory.InventoryResponse;
import com.vinsguru.common.messages.payment.PaymentResponse;
import com.vinsguru.common.messages.shipping.ShippingResponse;
import com.vinsguru.common.orchestrator.WorkflowOrchestrator;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface OrderFulfillmentOrchestrator extends WorkflowOrchestrator {

    Publisher<Request> orderInitialRequests();

    @Override
    default Publisher<Request> orchestrate(Response response) {
        if (response instanceof PaymentResponse e) {
            return handle(e);
        } else if (response instanceof InventoryResponse e) {
            return handle(e);
        }  else if (response instanceof ShippingResponse e) {
            return handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + response.getClass()));
        }
    }

    Publisher<Request> handle(PaymentResponse response);

    Publisher<Request> handle(InventoryResponse response);

    Publisher<Request> handle(ShippingResponse response);

}