package com.security.jwtdemo.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private  Long id; // MySQL Id
    private  String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private  String description;// from MongoDb
    private Map<String,Object> attributes; // from MongoDb
    private List<String> tags; // from MongoDb

}
