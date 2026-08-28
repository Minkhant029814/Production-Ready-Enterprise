package com.security.jwtdemo.dto.orderDto;

import java.util.List;
import java.util.Map;

public record CreateOrderRequest(
        List<OrderItemRequest> items,
        String paymentMethod,
        Map<String,Object> shippingAddress
) {
}
