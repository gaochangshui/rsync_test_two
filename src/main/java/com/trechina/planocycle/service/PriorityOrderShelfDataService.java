package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictJanDto;

import java.util.List;
import java.util.Map;

public interface PriorityOrderShelfDataService {

    /**
     * 新規では基本的なパター制約に関する情報を入手
     * @param companyCd
     * @return
     */
    Map<String,Object> getRestrictData(String companyCd,Integer priorityOrderCd);

    /**
     * 新規では基本パター制約別janの詳細情報を取得
     * @param
     * @return
     */
    Map<String,Object> getRestrictJans( PriorityOrderRestDto priorityOrderRestDto);


    /**
     * 新規では基本的なパタ台棚別jansの詳細情報を入手
     * @param priorityOrderPlatformShedDto
     * @return
     */
    Map<String,Object> getPlatformShedJans( PriorityOrderPlatformShedDto priorityOrderPlatformShedDto);
    /**
     * faceNumの保存
     * @param priorityOrderRestrictJanDto
     * @return
     */
    Map<String,Object> setFaceNumForData( List<PriorityOrderRestrictJanDto> priorityOrderRestrictJanDto);

    /**
     *
     */
    List<Map<String ,Object>> getPtsGroup(String companyCd, Integer priorityOrderCd,String tableName);
    /**
     * 新規では基本的なパター棚別の情報を取得
     * @param companyCd
     * @return
     */
    Map<String, Object> getNewPlatformShedData(String companyCd, Integer priorityOrderCd);

    Map<String, Object> getPtsJanInfo(String companyCd, Integer priorityOrderCd);

    Map<String, Object> setFaceNumAndPositionForData(Map<String,Object> map);

    Map<String, Object> getPtsAll(String companyCd, Integer priorityOrderCd);

}
