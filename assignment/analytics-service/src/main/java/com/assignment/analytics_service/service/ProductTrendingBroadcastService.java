package com.assignment.analytics_service.service;

import com.assignment.analytics_service.dto.ProductTrendingDto;
import com.assignment.analytics_service.repository.ProductViewRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

@Service
public class ProductTrendingBroadcastService {

    private final ProductViewRepository repository;
    private Flux<List<ProductTrendingDto>> trends;

    public ProductTrendingBroadcastService(ProductViewRepository repository) {
        this.repository = repository;
    }


    @PostConstruct
    private void init(){
        this.trends = this.repository.findTop5ByOrderByCountDesc()
                .map(pvc -> new ProductTrendingDto(pvc.getId(), pvc.getCount()))
                .collectList()
                .filter(Predicate.not(List::isEmpty))
                .repeatWhen(l -> l.delayElements(Duration.ofSeconds(3)))
                .distinctUntilChanged()
                .cache(1);
    }

    public Flux<List<ProductTrendingDto>> getTrends(){
        return this.trends;
    }

}