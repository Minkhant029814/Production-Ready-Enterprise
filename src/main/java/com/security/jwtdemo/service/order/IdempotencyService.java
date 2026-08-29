package com.security.jwtdemo.service.order;

import com.security.jwtdemo.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
   private  final StringRedisTemplate redisTemplate;

   private  static  final Duration KEY_TTL = Duration.ofMinutes(10);
   private  static  final  String REDIS_KEY_PREFIX = "idempotency:";

    public  void validateAndStoreKey(String idempotencyKey){

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequestException("Idempotency-Key header is missing!");
        }

        String redisKey = REDIS_KEY_PREFIX + idempotencyKey;

        /*
        * Redis SETNX: Key မရှိသေးရင် ထည့်မယ်။ TTL 10 မိနစ်သတ်မှတ်မယ်။
        * Return True -> အသစ်ထည့်၍ ရသွားရင် (first Time request)
        *Return False -> Key ရှိပြီးသားဖြစ်သည် (Duplicate request)
        * */

        Boolean isFirstRequest = redisTemplate.opsForValue()
                .setIfAbsent(redisKey,"LOCKED",KEY_TTL);
        if(Boolean.FALSE.equals(isFirstRequest)){
            throw new BadRequestException("Duplicate request detected. Order is already being processed or completed");
        }


    }

    public  void removeKey(String idempotencyKey){
        if(idempotencyKey != null && !idempotencyKey.isBlank()){
            redisTemplate.delete(REDIS_KEY_PREFIX + idempotencyKey);
        }
    }
}
