package com.example.service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang
 * date: 2020/12/7 11:37
 * description:
 */
public interface RedisService {

   void insertBloomFilter(String key, String element, Date expireDate);

    boolean mayExist(String key, String element);
}
