package com.vinsguru.order.application.service;

import com.vinsguru.order.application.entity.PurchaseOrder;
import com.vinsguru.order.application.mapper.EntityDtoMapper;
import com.vinsguru.order.application.repository.PurchaseOrderRepository;
import com.vinsguru.order.common.dto.OrderCreateRequest;
import com.vinsguru.order.common.dto.OrderDetails;
import com.vinsguru.order.common.dto.PurchaseOrderDto;
import com.vinsguru.order.common.service.OrderEventListener;
import com.vinsguru.order.common.service.OrderService;
import com.vinsguru.order.common.service.inventory.InventoryComponentFetcher;
import com.vinsguru.order.common.service.payment.PaymentComponentFetcher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final PurchaseOrderRepository repository;
    private final OrderEventListener eventListener;
    private final PaymentComponentFetcher paymentComponentFetcher;
    private final InventoryComponentFetcher inventoryComponentFetcher;

    public OrderServiceImpl(PurchaseOrderRepository repository, OrderEventListener eventListener, PaymentComponentFetcher paymentComponentFetcher, InventoryComponentFetcher inventoryComponentFetcher) {
        this.repository = repository;
        this.eventListener = eventListener;
        this.paymentComponentFetcher = paymentComponentFetcher;
        this.inventoryComponentFetcher = inventoryComponentFetcher;
    }

    @Override
    public Mono<PurchaseOrderDto> placeOrder(OrderCreateRequest request) {
        PurchaseOrder entity = EntityDtoMapper.toPurchaseOrder(request);
        return repository.save(entity)
                .map(EntityDtoMapper::toPurchaseOrderDto)
                .doOnNext(eventListener::emitOrderCreated);
    }

    @Override
    public Flux<PurchaseOrderDto> getAllOrders() {
        return repository.findAll()
                .map(EntityDtoMapper::toPurchaseOrderDto);
    }

    @Override
    public Mono<OrderDetails> getOrderDetails(UUID orderId) {
        return repository.findById(orderId)
                .map(EntityDtoMapper::toPurchaseOrderDto)
                .flatMap(dto -> paymentComponentFetcher.getComponent(orderId)
                        .zipWith(inventoryComponentFetcher.getComponent(orderId))
                        .map(t -> EntityDtoMapper.toOrderDetails(dto, t.getT1(), t.getT2()))
                );
    }
}
