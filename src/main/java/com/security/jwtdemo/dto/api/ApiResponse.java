package com.security.jwtdemo.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Not to put Null field to Json Output
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timeStamp
) {
    //Helper method for Success Response
    public static <T> ApiResponse<T> success(String message,T data){
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public  static  <T> ApiResponse<T> success(T data){
        return  success("Operation successful", data);
    }

    // Helper method for error response
    public  static <T> ApiResponse<T> error(String message){
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
