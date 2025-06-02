package com.assignment.analytics_service.controller;

import com.assignment.analytics_service.dto.ProductTrendingDto;
import com.assignment.analytics_service.service.ProductTrendingBroadcastService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("trending")
public class TrendingController {

    private final ProductTrendingBroadcastService broadcastService;

    public TrendingController(ProductTrendingBroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<ProductTrendingDto>> trending(){
        return this.broadcastService.getTrends();
    }


}