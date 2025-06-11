package com.vinsguru.order.messaging.orchestrator;

import com.vinsguru.common.messages.Request;
import com.vinsguru.common.messages.inventory.InventoryRequest;
import com.vinsguru.common.messages.inventory.InventoryResponse;
import com.vinsguru.common.orchestrator.WorkflowStep;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface InventoryStep extends WorkflowStep<InventoryResponse> {

    @Override
    default Publisher<Request> process(InventoryResponse response) {

        if (response instanceof InventoryResponse.Deducted e) {
            return onSuccess(e);
        } else if (response instanceof InventoryResponse.Declined e) {
            return onFailure(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + response.getClass()));
        }

    }

    Publisher<Request> onSuccess(InventoryResponse.Deducted response);

    Publisher<Request> onFailure(InventoryResponse.Declined response);

}