package com.security.jwtdemo.dto.orderDto;

public record OrderItemRequest(
        Long productId,
        Integer quantity
) {
}
