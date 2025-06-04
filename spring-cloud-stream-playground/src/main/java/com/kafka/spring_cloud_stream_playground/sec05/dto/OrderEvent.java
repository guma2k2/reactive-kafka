package com.kafka.spring_cloud_stream_playground.sec05.dto;

public record OrderEvent(int customerId,
                         int productId,
                         OrderType orderType) {
}