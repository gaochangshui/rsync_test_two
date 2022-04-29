package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.entity.dto.ProductPowerGroupDataForCgiDto;

import java.util.Map;

public interface CommodityScoreDataService {

    /**
     * 商品力点数表の基本データの取得
     *
     * @param taskID
     * @return
     */
    Map<String, Object> getCommodityScoreData(String taskID, String companyCd);

    /**
     * 商品力点数表顧客Groupデータ取得
     *
     * @param taskID
     * @return
     */
    Map<String, Object> getCommodityScoreGroupData(String taskID, String companyCd);

    /**
     * 查看自动计算执行状态
     * @param taskId
     * @return
     */
    Map<String, Object> productTaskId(String taskId);
    /**
     * 商品力点数表基本taskidを取得する
     *
     * @param productPowerDataForCgiDto
     * @return
     */
    Map<String, Object> getCommodityScoreTaskId(ProductPowerDataForCgiDto productPowerDataForCgiDto);

    /**
     * 商品パワーポイントリストを取得するお客様Grouptaskid
     *
     * @param productPowerDataForCgiDto
     * @return
     */
    Map<String, Object> getCommodityScoreGroupTaskId(ProductPowerGroupDataForCgiDto productPowerDataForCgiDto);


}
