package com.security.jwtdemo.respository.mysqlRepository;

import com.security.jwtdemo.entity.mysql.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserId (Long userId);
}
