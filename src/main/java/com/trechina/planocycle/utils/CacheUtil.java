package com.trechina.planocycle.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CacheUtil {

    private Cache<String,Object> cache = CacheBuilder.newBuilder()
            .recordStats()
            .maximumSize(5000000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();
    /**
     * 返回是否存在車輛，非null為存在
     * @return
     */
    public Object get(String key){
        return cache.getIfPresent(key);
    }
    /**
     * 返回是否存在車輛，非null為存在
     * @return
     */
    public void put(String key, Object value){
        cache.put(key,value);

    }
    public void remove(String key){
        cache.invalidate(key);

    }

}