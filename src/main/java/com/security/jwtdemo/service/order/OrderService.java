package com.security.jwtdemo.service.order;

import com.security.jwtdemo.config.RabbitMQConfig;
import com.security.jwtdemo.dto.event.OrderCreatedEvent;
import com.security.jwtdemo.dto.orderDto.CreateOrderRequest;
import com.security.jwtdemo.dto.orderDto.OrderItemRequest;
import com.security.jwtdemo.dto.orderDto.OrderResponse;
import com.security.jwtdemo.entity.mongo.OrderLog;
import com.security.jwtdemo.entity.mysql.Order;
import com.security.jwtdemo.entity.mysql.OrderItem;
import com.security.jwtdemo.entity.mysql.OrderStatus;
import com.security.jwtdemo.entity.mysql.Product;
import com.security.jwtdemo.exception.BadRequestException;
import com.security.jwtdemo.exception.ResourceNotFoundException;
import com.security.jwtdemo.respository.mongoRepository.OrderLogRepository;
import com.security.jwtdemo.respository.mysqlRepository.OrderRepository;
import com.security.jwtdemo.respository.mysqlRepository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.productId()));

            // Stock စစ်ဆေးခြင်း
            if (product.getStockQuantity() < itemRequest.quantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            // Stock လျှော့ခြင်း (Loop အပြင်မှ အစုလိုက် saveAll ပြုလုပ်ရန် List ထဲ ထည့်ထားမည်)
            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());
            productsToUpdate.add(product);
            productRepository.saveAll(productsToUpdate);

            // Item တစ်ခုချင်းစီ၏ Dynamic Price တွက်ချက်ခြင်း
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .quantity(itemRequest.quantity())
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);
        }

//        // Product Stock များကို Batch Update လုပ်ခြင်း
//        productRepository.saveAll(productsToUpdate);

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Order ကို MySQL သို့ အရင် Save ခြင်း
        Order savedOrder = orderRepository.save(order);

        // Spring Internal Event ထုတ်လွှင့်ခြင်း (Transaction Commit ဖြစ်ရန် စောင့်မည်)
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                userId,
                request.paymentMethod(),
                request.shippingAddress(),
                LocalDateTime.now()
        );
        eventPublisher.publishEvent(event);

        return mapToOrderResponse(savedOrder, request.paymentMethod(), request.shippingAddress());
    }

    private OrderResponse mapToOrderResponse(Order order, String paymentMethod, Map<String, Object> shippingAddress) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getStatus())
                .createdAt(order.getCreatedAt())
                .shippingAddress(shippingAddress)
                .build();
    }
}