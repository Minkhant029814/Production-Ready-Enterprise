package com.security.jwtdemo.dto.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public record OrderCreatedEvent(
        Long mysqlOrderId,
        Long userId,
        String paymentMethod,
        Map<String, Object> shippingAddress,
        LocalDateTime timestamp
) implements Serializable {
}
