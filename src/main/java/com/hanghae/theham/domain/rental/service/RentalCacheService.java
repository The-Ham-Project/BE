package com.hanghae.theham.domain.rental.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RentalCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    public RentalCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void evictAllCachesForRental(Long rentalId) {
        Set<String> keys = redisTemplate.keys("Rentals::" + rentalId + ",*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
