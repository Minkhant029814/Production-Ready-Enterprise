package com.security.jwtdemo.event.listener;

import com.security.jwtdemo.config.RabbitMQConfig;
import com.security.jwtdemo.dto.event.OrderCreatedEvent;
import com.security.jwtdemo.entity.mongo.OrderLog;
import com.security.jwtdemo.respository.mongoRepository.OrderLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderLogRepository orderLogRepository;

    // Background တွင် အလိုအလျောက် RabbitMQ Message ကို ယူ၍ MongoDB တွင် သိမ်းပေးမည်
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received Order Created Event for Order ID: {}", event.mysqlOrderId());

        OrderLog orderLog = OrderLog.builder()
                .mysqlOrderId(event.mysqlOrderId())
                .userId(event.userId())
                .paymentMethod(event.paymentMethod())
                .shippingAddress(event.shippingAddress())
                .logMessage("Order created asynchronously via RabbitMQ Event")
                .timestamp(event.timestamp())
                .build();

        orderLogRepository.save(orderLog);
        log.info("Successfully saved OrderLog to MongoDB asynchronously!");
    }
}