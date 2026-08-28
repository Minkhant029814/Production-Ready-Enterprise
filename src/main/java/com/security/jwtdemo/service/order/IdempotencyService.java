package com.security.jwtdemo.service.order;

import com.security.jwtdemo.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {
    private final Map<String,Boolean> keyStore = new ConcurrentHashMap<>();

    public  void validateAndStoreKey(String idempotencyKey){

        if(idempotencyKey.isBlank() || idempotencyKey == null){
            throw new BadRequestException("Idempotency-Key header is missing!");
        }

        if(keyStore.containsKey(idempotencyKey)){
            throw new BadRequestException("Duplicate request detected. Order is already being processed.");
        }

        keyStore.put(idempotencyKey,true);
    }
}
