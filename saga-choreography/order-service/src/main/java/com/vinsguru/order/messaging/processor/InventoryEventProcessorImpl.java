package com.vinsguru.order.messaging.processor;


import com.vinsguru.common.events.inventory.InventoryEvent;
import com.vinsguru.common.events.order.OrderEvent;
import com.vinsguru.common.processor.InventoryEventProcessor;
import com.vinsguru.order.common.dto.OrderInventoryDto;
import com.vinsguru.order.common.service.OrderFulfillmentService;
import com.vinsguru.order.common.service.inventory.InventoryComponentStatusListener;
import com.vinsguru.order.messaging.mapper.InventoryEventMapper;
import com.vinsguru.order.messaging.mapper.OrderEventMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class InventoryEventProcessorImpl implements InventoryEventProcessor<OrderEvent> {

    private final OrderFulfillmentService fulfillmentService;
    private final InventoryComponentStatusListener statusListener;


    public InventoryEventProcessorImpl(OrderFulfillmentService fulfillmentService, InventoryComponentStatusListener statusListener) {
        this.fulfillmentService = fulfillmentService;
        this.statusListener = statusListener;
    }

    @Override
    public Mono<OrderEvent> handle(InventoryEvent.InventoryDeducted event) {
        OrderInventoryDto dto = InventoryEventMapper.toDto(event);
        return statusListener.onSuccess(dto)
                .then(fulfillmentService.complete(event.orderId()))
                .map(OrderEventMapper::toOrderCompletedEvent);
    }

    @Override
    public Mono<OrderEvent> handle(InventoryEvent.InventoryDeclined event) {
        OrderInventoryDto orderInventoryDto = InventoryEventMapper.toDto(event);
        return statusListener.onFailure(orderInventoryDto)
                .then(fulfillmentService.cancel(event.orderId()))
                .map(OrderEventMapper::toOrderCancelledEvent);
    }

    @Override
    public Mono<OrderEvent> handle(InventoryEvent.InventoryRestored event) {
        var dto = InventoryEventMapper.toDto(event);
        return this.statusListener.onRollback(dto)
                .then(Mono.empty());
    }
}
