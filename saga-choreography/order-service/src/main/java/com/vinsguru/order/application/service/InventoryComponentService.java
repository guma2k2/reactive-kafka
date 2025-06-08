package com.vinsguru.order.application.service;


import com.vinsguru.order.application.entity.OrderInventory;
import com.vinsguru.order.application.mapper.EntityDtoMapper;
import com.vinsguru.order.application.repository.OrderInventoryRepository;
import com.vinsguru.order.common.dto.OrderInventoryDto;
import com.vinsguru.order.common.service.inventory.InventoryComponentFetcher;
import com.vinsguru.order.common.service.inventory.InventoryComponentStatusListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class InventoryComponentService implements InventoryComponentFetcher, InventoryComponentStatusListener {


    private static final OrderInventoryDto DEFAULT = OrderInventoryDto.builder().build();
    private final OrderInventoryRepository orderInventoryRepository;

    public InventoryComponentService(OrderInventoryRepository orderInventoryRepository) {
        this.orderInventoryRepository = orderInventoryRepository;
    }


    @Override
    public Mono<OrderInventoryDto> getComponent(UUID orderId) {
        return orderInventoryRepository.findByOrderId(orderId)
                .map(EntityDtoMapper::toOrderInventoryDto)
                .defaultIfEmpty(DEFAULT);
    }

    @Override
    public Mono<Void> onSuccess(OrderInventoryDto message) {
        return orderInventoryRepository.findByOrderId(message.orderId())
                .switchIfEmpty(Mono.defer(() -> add(message, true)))
                .then();
    }

    private Mono<OrderInventory> add(OrderInventoryDto dto, boolean isSuccess) {
        OrderInventory orderInventory = EntityDtoMapper.toOrderInventory(dto);
        orderInventory.setSuccess(isSuccess);
        return orderInventoryRepository.save(orderInventory);
    }

    @Override
    public Mono<Void> onFailure(OrderInventoryDto message) {
        return orderInventoryRepository.findByOrderId(message.orderId())
                .switchIfEmpty(Mono.defer(() -> add(message, false)))
                .then();
    }

    @Override
    public Mono<Void> onRollback(OrderInventoryDto message) {
        return orderInventoryRepository.findByOrderId(message.orderId())
                .doOnNext(e -> e.setStatus(message.status()))
                .flatMap(orderInventoryRepository::save)
                .then();
    }
}
