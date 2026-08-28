package com.security.jwtdemo;


import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class JwtDemoApplication  {

    @PostConstruct
    public  void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Yangon"));
    }
    public static void main(String[] args) {
        SpringApplication.run(JwtDemoApplication.class, args);
    }







}
