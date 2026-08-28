package com.security.jwtdemo.dto.orderDto;

import com.security.jwtdemo.entity.mysql.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class OrderRequest {
    private Long orderId;
    private  Long userId;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private  String paymentMethod;
    private Map<String,Object> shippingAddress;

}
