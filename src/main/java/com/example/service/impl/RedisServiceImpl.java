package com.example.service.impl;

import com.example.bloomFilter.RedisBloomFilter;
import com.example.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang
 * date: 2020/12/7 11:38
 * description:
 */
@Service
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    RedisBloomFilter redisBloomFilter;

    public RedisServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        redisBloomFilter = new RedisBloomFilter(stringRedisTemplate,300000000, 0.01);
    }

    @Override
    public void insertBloomFilter(String key, String element, Date expireDate) {
        RedisBloomFilter redisBloomFilter = new RedisBloomFilter(stringRedisTemplate,300000000, 0.01);
        redisBloomFilter.insert(key, element, RedisBloomFilter.getTwelveTime());
    }

    @Override
    public boolean mayExist(String key, String element) {
       return redisBloomFilter.mayExist(key, element);
    }
}
