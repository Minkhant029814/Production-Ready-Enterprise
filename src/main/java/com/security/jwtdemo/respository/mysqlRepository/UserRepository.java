package com.security.jwtdemo.respository.mysqlRepository;

import com.security.jwtdemo.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

}
