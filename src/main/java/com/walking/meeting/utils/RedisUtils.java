package com.walking.meeting.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

public class RedisUtils {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void set(String key, String value) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public void setex(String key, String value, int seconds) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, seconds);
    }

}

