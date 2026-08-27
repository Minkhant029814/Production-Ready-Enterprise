package com.security.jwtdemo.respository.mysqlRepository;

import com.security.jwtdemo.entity.mysql.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
}
