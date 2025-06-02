package com.assignment.product_service.util;

import com.assignment.product_service.dto.ProductDto;
import com.assignment.product_service.entity.Product;
import org.springframework.beans.BeanUtils;

public class EntityDtoUtil {
    public static ProductDto toDto(Product product){
        var dto = new ProductDto();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }
}
