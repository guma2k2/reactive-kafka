package com.vinsguru.payment.messaging.processor;

import com.vinsguru.common.messages.payment.PaymentRequest;
import com.vinsguru.common.messages.payment.PaymentResponse;
import com.vinsguru.common.processor.RequestProcessor;
import reactor.core.publisher.Mono;

public interface PaymentRequestProcessor extends RequestProcessor<PaymentRequest, PaymentResponse> {

    @Override
    default Mono<PaymentResponse> process(PaymentRequest request) {
        if (request instanceof PaymentRequest.Process e) {
            return handle(e);
        } else if (request instanceof PaymentRequest.Refund e) {
            return handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + request.getClass()));
        }
    }

    Mono<PaymentResponse> handle(PaymentRequest.Process request);

    Mono<PaymentResponse> handle(PaymentRequest.Refund request);

}