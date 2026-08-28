package com.security.jwtdemo.entity.mongo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_logs")
public class OrderLog {

    @Id
    private String id;
    private Long mysqlOrderId;
    private Long userId;
    private String paymentMethod;
    private Map<String, Object> shippingAddress;
    private String logMessage;
    private LocalDateTime timestamp;
}
