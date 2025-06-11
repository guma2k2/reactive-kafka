package com.vinsguru.order.messaging.orchestrator;

import com.vinsguru.common.messages.Request;
import com.vinsguru.common.messages.inventory.InventoryResponse;
import com.vinsguru.common.messages.payment.PaymentResponse;
import com.vinsguru.common.orchestrator.WorkflowStep;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface PaymentStep extends WorkflowStep<PaymentResponse> {

    @Override
    default Publisher<Request> process(PaymentResponse response) {


        if (response instanceof PaymentResponse.Processed e) {
            return onSuccess(e);
        } else if (response instanceof PaymentResponse.Declined e) {
            return onFailure(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + response.getClass()));
        }
    }

    Publisher<Request> onSuccess(PaymentResponse.Processed response);

    Publisher<Request> onFailure(PaymentResponse.Declined response);

}