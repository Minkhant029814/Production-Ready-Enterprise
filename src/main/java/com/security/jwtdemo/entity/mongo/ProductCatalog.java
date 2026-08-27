package com.security.jwtdemo.entity.mongo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product_catalog")
public class ProductCatalog {

    @Id
    private  String id;

    private  Long mysqlProductId;
    private String description;
    private Map<String,Object> attributes;
    private List<String> tags;
    private  Double averageRating;

}
