package com.hanghae.theham.domain.rental.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RentalCachingService {

    private final RedisTemplate<String, String> redisTemplate;

    public RentalCachingService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void deleteKeys(Long rentalId) {
        Set<String> keys = redisTemplate.keys("Rentals::%d,*".formatted(rentalId));
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
