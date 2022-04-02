package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictJanDto;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface PriorityOrderShelfDataService {

    /**
     * 新规では基本的なパター制約に関する情報を入手
     * @param companyCd
     * @return
     */
    Map<String,Object> getRestrictData(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     * 新规では基本パター制約别janの详細情報を取得
     * @param
     * @return
     */
    Map<String,Object> getRestrictJans( PriorityOrderRestDto priorityOrderRestDto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    /**
     * 新规では基本的なパター棚别の情报を取得
     * @param companyCd
     * @return
     */
    Map<String,Object> getPlatformShedData(String companyCd,Integer priorityOrderCd);
    /**
     * 新规では基本的なパタ台棚别jansの详細情报を入手
     * @param priorityOrderPlatformShedDto
     * @return
     */
    Map<String,Object> getPlatformShedJans( PriorityOrderPlatformShedDto priorityOrderPlatformShedDto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    /**
     * faceNumの保存
     * @param priorityOrderRestrictJanDto
     * @return
     */
    Map<String,Object> setFaceNumForData( List<PriorityOrderRestrictJanDto> priorityOrderRestrictJanDto);
}
