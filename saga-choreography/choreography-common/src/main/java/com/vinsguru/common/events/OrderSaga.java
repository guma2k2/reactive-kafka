package com.vinsguru.common.events;
import java.util.UUID;
public interface OrderSaga extends Saga{

    UUID orderId();
}
