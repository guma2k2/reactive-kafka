package com.vinsguru.events;

import java.time.Instant;

public interface DomainEvent {

    Instant createdAt();

}