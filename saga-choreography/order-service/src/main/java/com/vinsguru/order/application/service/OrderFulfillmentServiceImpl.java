package com.vinsguru.order.application.service;

import com.vinsguru.common.events.order.OrderStatus;
import com.vinsguru.order.application.entity.PurchaseOrder;
import com.vinsguru.order.application.mapper.EntityDtoMapper;
import com.vinsguru.order.application.repository.PurchaseOrderRepository;
import com.vinsguru.order.common.dto.PurchaseOrderDto;
import com.vinsguru.order.common.service.OrderFulfillmentService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.UUID;
import java.util.function.Function;


@Service
public class OrderFulfillmentServiceImpl implements OrderFulfillmentService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public OrderFulfillmentServiceImpl(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public Mono<PurchaseOrderDto> complete(UUID orderId) {
        return purchaseOrderRepository.getWhenOrderComponentsCompleted(orderId)
                .transform(updateStatus(OrderStatus.COMPLETED));
    }

    @Override
    public Mono<PurchaseOrderDto> cancel(UUID orderId) {
        return purchaseOrderRepository.findByOrderIdAndStatus(orderId, OrderStatus.PENDING)
                .transform(updateStatus(OrderStatus.CANCELLED));
    }

    private Function<Mono<PurchaseOrder>, Mono<PurchaseOrderDto>> updateStatus(OrderStatus status) {
        return mono -> mono
                .doOnNext(e -> e.setStatus(status))
                .flatMap(purchaseOrderRepository::save)
                .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance))
                .map(EntityDtoMapper::toPurchaseOrderDto);
    }
}
