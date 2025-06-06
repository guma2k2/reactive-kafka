package com.vinsguru.publisher;

import com.vinsguru.events.DomainEvent;
import reactor.core.publisher.Flux;

public interface EventPublisher<T extends DomainEvent> {

    Flux<T> publish();

}