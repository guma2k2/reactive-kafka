package com.assignment.product_service.service;

import com.assignment.product_service.dto.ProductDto;
import com.assignment.product_service.event.ProductViewEvent;
import com.assignment.product_service.repository.ProductRepository;
import com.assignment.product_service.util.EntityDtoUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductViewEventProducer productViewEventProducer;

    public ProductService(ProductRepository repository, ProductViewEventProducer productViewEventProducer) {
        this.repository = repository;
        this.productViewEventProducer = productViewEventProducer;
    }

    public Mono<ProductDto> getProduct(int id){
        return this.repository.findById(id)
                .doOnNext(e -> this.productViewEventProducer.emitEvent(new ProductViewEvent(e.getId())))
                .map(EntityDtoUtil::toDto);
    }

}