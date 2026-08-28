package com.security.jwtdemo.respository.mongoRepository;

import com.security.jwtdemo.entity.mongo.OrderLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderLogRepository extends MongoRepository<OrderLog,String> {
    Optional<OrderLog> findByMysqlOrderId(Long mysqlOrderId);
}
