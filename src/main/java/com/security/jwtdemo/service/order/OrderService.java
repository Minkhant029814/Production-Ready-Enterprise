package com.security.jwtdemo.service.order;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private  final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderLogRepository orderLogRepository;

    @Transactional
    public  OrderResponse createOrder(Long userId, CreateOrderRequest request){
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        for(OrderItemRequest itemRequest: request.items()){
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(()-> new ResourceNotFoundException("Product not found with ID: " + itemRequest.productId()));

            //Checking sufficient stock level
            if(product.getStockQuantity() < itemRequest.quantity()){
                throw new BadRequestException("Insufficient stock for product :" + product.getName());
            }

            //Reduce stock level
            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());
            productRepository.save(product);

            //Calculate total amount for each item

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
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        //Storing Order to Mysql Database
        Order savedOrder = orderRepository.save(order);

        //Storing Snapshot Audit Log into MongoDB

        OrderLog orderLog = OrderLog.builder()
                .mysqlOrderId(savedOrder.getId())
                .userId(userId)
                .paymentMethod(request.paymentMethod())
                .shippingAddress(request.shippingAddress())
                .logMessage("Order created successfully with status PENDING")
                .timestamp(LocalDateTime.now())
                .build();
        orderLogRepository.save(orderLog);

        return maptoOrderResponse(savedOrder,orderLog);
    }



    //helper method to map orderResponse Format
    private OrderResponse maptoOrderResponse(Order order, OrderLog orderLog){
        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getStatus())
                .createdAt(order.getCreatedAt())
                .shippingAddress(orderLog.getShippingAddress())
                .build();
    }
}
