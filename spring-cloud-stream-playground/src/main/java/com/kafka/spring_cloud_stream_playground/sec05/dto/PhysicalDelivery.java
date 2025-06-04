package com.kafka.spring_cloud_stream_playground.sec05.dto;

public record PhysicalDelivery(int productId,
                               String street,
                               String city,
                               String country) {
}