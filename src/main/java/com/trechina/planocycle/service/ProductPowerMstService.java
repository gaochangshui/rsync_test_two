package com.trechina.planocycle.service;

import java.util.Map;

public interface ProductPowerMstService {
    /**
     * 根据企业cd获取商品力点数表/基本pattern一览
     * @param companyCd
     * @return
     */
    Map<String,Object> getTableName(String companyCd);
    /**
     * 根据企业cd获取商品力点数表一览
     * @param companyCd
     * @return
     */
    Map<String,Object> getProductPowerTable(String companyCd);

     Map<String,Object> getProductPowerInfo(String companyCd ,Integer productPowerCd,Integer priorityOrderCd);
}
