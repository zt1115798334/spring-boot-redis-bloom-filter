package com.example.controller;

import com.example.bloomFilter.RedisBloomFilter;
import com.example.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang
 * date: 2020/12/7 13:06
 * description:
 */
@RestController
public class TestController {

    private final RedisService redisService;

    public TestController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/bloom")
    public void insertUserT() {
        //大概3百万数据，误差率在10%作用。
        redisService.insertBloomFilter("topic_read:20200812", "da0b7ba24c4ae7cdcccba20f3bd3152d", RedisBloomFilter.getTwelveTime());
        redisService.insertBloomFilter("topic_read:20200812", "da0b7ba24c4ae7cdcccba20f3bd31521", RedisBloomFilter.getTwelveTime());
        redisService.insertBloomFilter("topic_read:20200812", "76930244", RedisBloomFilter.getTwelveTime());
        redisService.insertBloomFilter("topic_read:20200812", "76930245", RedisBloomFilter.getTwelveTime());
        redisService.insertBloomFilter("topic_read:20200812", "76930246", RedisBloomFilter.getTwelveTime());

        System.out.println(redisService.mayExist("topic_read:20200812", "da0b7ba24c4ae7cdcccba20f3bd31522"));
        System.out.println(redisService.mayExist("topic_read:20200812", "da0b7ba24c4ae7cdcccba20f3bd31523"));
        System.out.println(redisService.mayExist("topic_read:20200812", "76930246"));
        System.out.println(redisService.mayExist("topic_read:20200812", "76930248"));
        System.out.println(redisService.mayExist("topic_read:20200812", "769302428"));
    }
}
