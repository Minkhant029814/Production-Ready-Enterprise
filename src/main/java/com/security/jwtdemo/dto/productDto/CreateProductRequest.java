package com.security.jwtdemo.dto.productDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CreateProductRequest(
        String name,
        BigDecimal price,
        Integer stockQuantity,
        String description,
        Map<String,Object> attributes,
        List<String> tags
) {
}
