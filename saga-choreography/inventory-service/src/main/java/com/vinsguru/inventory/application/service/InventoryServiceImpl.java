package com.vinsguru.inventory.application.service;

import com.vinsguru.common.events.inventory.InventoryStatus;
import com.vinsguru.common.util.DuplicateEventValidator;
import com.vinsguru.inventory.application.entity.OrderInventory;
import com.vinsguru.inventory.application.entity.Product;
import com.vinsguru.inventory.application.mapper.EntityDtoMapper;
import com.vinsguru.inventory.application.repository.InventoryRepository;
import com.vinsguru.inventory.application.repository.ProductRepository;
import com.vinsguru.inventory.common.dto.InventoryDeductRequest;
import com.vinsguru.inventory.common.dto.OrderInventoryDto;
import com.vinsguru.inventory.common.exception.OutOfStockException;
import com.vinsguru.inventory.common.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private static final Mono<Product> OUT_OF_STOCK = Mono.error(new OutOfStockException());
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public Mono<OrderInventoryDto> deduct(InventoryDeductRequest request) {
        return DuplicateEventValidator.validate(
                inventoryRepository.existsByOrderId(request.orderId()),
                productRepository.findById(request.productId())
        )
                .filter(p -> p.getAvailableQuantity() > request.quantity())
                .switchIfEmpty(OUT_OF_STOCK)
                .flatMap(p -> deductInventory(p, request))
                .doOnNext(dto -> log.info("inventory deducted for {}", dto.orderId()));
    }

    private Mono<OrderInventoryDto> deductInventory(Product product, InventoryDeductRequest request) {
        OrderInventory orderInventory = EntityDtoMapper.toOrderInventory(request);
        product.setAvailableQuantity(product.getAvailableQuantity() - request.quantity());
        orderInventory.setStatus(InventoryStatus.DEDUCTED);
        return productRepository.save(product)
                .then(inventoryRepository.save(orderInventory))
                .map(EntityDtoMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<OrderInventoryDto> restore(UUID orderId) {
        return inventoryRepository.findByOrderIdAndStatus(orderId, InventoryStatus.DEDUCTED)
                .zipWhen(i -> productRepository.findById(i.getProductId()))
                .flatMap(t -> restoreInventory(t.getT1(), t.getT2()))
                .doOnNext(dto -> log.info("restored quantity {} for {}", dto.quantity(), dto.orderId()));
    }

    private Mono<OrderInventoryDto> restoreInventory(OrderInventory orderInventory, Product product) {
        product.setAvailableQuantity(product.getAvailableQuantity() + orderInventory.getQuantity());
        orderInventory.setStatus(InventoryStatus.RESTORED);
        return productRepository.save(product)
                .then(inventoryRepository.save(orderInventory))
                .map(EntityDtoMapper::toDto);
    }
}
