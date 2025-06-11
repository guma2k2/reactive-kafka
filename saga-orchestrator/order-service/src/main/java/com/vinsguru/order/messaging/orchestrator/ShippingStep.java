
package com.vinsguru.order.messaging.orchestrator;

import com.vinsguru.common.messages.Request;
import com.vinsguru.common.messages.inventory.InventoryResponse;
import com.vinsguru.common.messages.shipping.ShippingResponse;
import com.vinsguru.common.orchestrator.WorkflowStep;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface ShippingStep extends WorkflowStep<ShippingResponse> {

    @Override
    default Publisher<Request> process(ShippingResponse response) {

        if (response instanceof ShippingResponse.Scheduled e) {
            return onSuccess(e);
        } else if (response instanceof ShippingResponse.Declined e) {
            return onFailure(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + response.getClass()));
        }
    }

    Publisher<Request> onSuccess(ShippingResponse.Scheduled response);

    Publisher<Request> onFailure(ShippingResponse.Declined response);

}