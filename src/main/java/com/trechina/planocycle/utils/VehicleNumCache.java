package com.trechina.planocycle.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class VehicleNumCache {

    private final Integer existFlag = new Integer(1);
    private Cache<String,Integer> cache = CacheBuilder.newBuilder()
            .recordStats()
            .maximumSize(5000000)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();
    /**
     * 返回是否存在车辆，非null为存在
     * @param vehicleNum
     * @return
     */
    public Integer get(String vehicleNum){
        return cache.getIfPresent(vehicleNum);
    }
    /**
     * 返回是否存在车辆，非null为存在
     * @param vehicleNum
     * @return
     */
    public void put(String vehicleNum){
        cache.put(vehicleNum,existFlag);

    }
    public void remove(String vehicleNum){
        cache.invalidate(vehicleNum);

    }

}