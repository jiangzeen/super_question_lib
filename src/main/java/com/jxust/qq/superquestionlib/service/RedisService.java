package com.jxust.qq.superquestionlib.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redis) {
        this.redisTemplate = redis;
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setKeyExpire(String key, long seconds) {
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public void setKeyValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void hSet(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }


}

