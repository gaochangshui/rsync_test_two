package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictDto;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface PriorityOrderShelfDataService {

    /**
     * 新规时获取基本パタ制约别信息
     * @param companyCd
     * @return
     */
    Map<String,Object> getRestrictData(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     * 新规时获取基本パタ制约别jan详细信息
     * @param priorityOrderRestrictDto
     * @return
     */
    Map<String,Object> getRestrictJans( PriorityOrderRestrictDto priorityOrderRestrictDto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    /**
     * 新规时获取基本パタ台棚别信息
     * @param companyCd
     * @return
     */
    Map<String,Object> getPlatformShedData(String companyCd,Integer priorityOrderCd);
    /**
     * 新规时获取基本パタ台棚别jans详细信息
     * @param priorityOrderPlatformShedDto
     * @return
     */
    Map<String,Object> getPlatformShedJans( PriorityOrderPlatformShedDto priorityOrderPlatformShedDto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
