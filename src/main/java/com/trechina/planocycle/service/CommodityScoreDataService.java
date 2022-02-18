package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;


import java.util.Map;

public interface CommodityScoreDataService {

    /**
     * rank计算后的数据加载接口
     * @param productPowerDataForCgiDto
     * @return
     */
    Map<String,Object> getCommodityScoreData(String taskID,Integer isPosOrClient);

    /**
     * 保存文件的时候调用cgi
     * @param companyCd
     * @param filename
     * @param datacd
     * @return
     */
    Map<String, Object> getAttrFileSaveForCgi(String companyCd, String filename, String datacd,
                                              Integer productPowerNo,String dataNm);

    /**
     * 获取商品力点数表taskid
     * @param productPowerDataForCgiDto
     * @return
     */
    Map<String, Object> getCommodityScoreTaskId(ProductPowerDataForCgiDto productPowerDataForCgiDto);
    /**
     * 商品力点数表编辑
     * @param productPowerDataForCgiDto
     * @return
     */
     Map<String,Object> updateCommodityScoreData( ProductPowerDataForCgiDto productPowerDataForCgiDto);

    /**
     * 获取DB商品力点数表data
     */
    Map<String,Object> getDBCommodityScoreData(String companyCd ,String productPowerNo);
}
