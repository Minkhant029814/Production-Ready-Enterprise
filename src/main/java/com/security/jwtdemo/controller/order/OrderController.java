package com.security.jwtdemo.controller.order;

import com.security.jwtdemo.dto.api.ApiResponse;
import com.security.jwtdemo.dto.orderDto.CreateOrderRequest;
import com.security.jwtdemo.dto.orderDto.OrderResponse;
import com.security.jwtdemo.entity.mysql.User;
import com.security.jwtdemo.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")

@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Order created successfully", response));
    }
}
