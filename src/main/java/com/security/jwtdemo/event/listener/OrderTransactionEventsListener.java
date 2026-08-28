package com.security.jwtdemo.event.listener;

import com.security.jwtdemo.config.RabbitMQConfig;
import com.security.jwtdemo.dto.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTransactionEventsListener {

    private final RabbitTemplate rabbitTemplate;

    // MySQL Transaction တကယ် အောင်မြင်စွာ Commit ဖြစ်သွားမှသာ RabbitMQ ထံ Message ပို့မည်
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedCommit(OrderCreatedEvent event) {
        log.info("MySQL Transaction Committed successfully. Publishing event to RabbitMQ for Order ID: {}", event.mysqlOrderId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}