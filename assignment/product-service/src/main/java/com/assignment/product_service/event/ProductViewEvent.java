package com.assignment.product_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class ProductViewEvent {

    private Integer productId;

    public ProductViewEvent(Integer productId) {
        this.productId = productId;
    }

    public ProductViewEvent() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}