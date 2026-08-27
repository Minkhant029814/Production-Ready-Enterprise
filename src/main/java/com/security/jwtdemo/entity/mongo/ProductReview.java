package com.security.jwtdemo.entity.mongo;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "product_reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductReview {
    @Id
    private  String id;

    private Long productId;
    private  Long userId;
    private  String username;
    private  Integer rating;
    private String comment;
    private LocalDateTime createdAt;

}
