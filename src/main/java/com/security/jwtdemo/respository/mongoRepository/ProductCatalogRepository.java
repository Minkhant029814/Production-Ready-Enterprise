package com.security.jwtdemo.respository.mongoRepository;

import com.security.jwtdemo.entity.mongo.ProductCatalog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductCatalogRepository extends MongoRepository<ProductCatalog,String> {
    Optional<ProductCatalog> findByMysqlProductId(Long mysqlProductId);
}
