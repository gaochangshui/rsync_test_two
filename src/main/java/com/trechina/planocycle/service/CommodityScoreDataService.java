package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.entity.dto.ProductPowerGroupDataForCgiDto;

import java.util.Map;

public interface CommodityScoreDataService {

    /**
     * 获取商品力点数表基本数据
     *
     * @param taskID
     * @return
     */
    Map<String, Object> getCommodityScoreData(String taskID, String companyCd);

    /**
     * 获取商品力点数表顾客Group数据
     *
     * @param taskID
     * @return
     */
    Map<String, Object> getCommodityScoreGroupData(String taskID, String companyCd);


    /**
     * 获取商品力点数表基本taskid
     *
     * @param productPowerDataForCgiDto
     * @return
     */
    Map<String, Object> getCommodityScoreTaskId(ProductPowerDataForCgiDto productPowerDataForCgiDto);

    /**
     * 获取商品力点数表顾客Grouptaskid
     *
     * @param productPowerDataForCgiDto
     * @return
     */
    Map<String, Object> getCommodityScoreGroupTaskId(ProductPowerGroupDataForCgiDto productPowerDataForCgiDto);


}
