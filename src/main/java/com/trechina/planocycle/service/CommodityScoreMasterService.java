package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.ProductCdAndNameDto;
import com.trechina.planocycle.entity.po.ProductPowerParamMst;

import java.util.Map;

public interface CommodityScoreMasterService {
    /**
     * つかむ取企業情報接口
     * @return
     */
    Map<String,Object> getEnterpriseInfo();


    /**
     * 保存商品力点数的模板名接口
     * @param productPowerName
     * @return
     */
    Map<String,Object> setCommodityList(ProductCdAndNameDto productPowerName);






    void productPowerParamAttrName(String conpanyCd, Integer productPowerCd, Map<String, Object> result);

    /**
     * 削除商品力点数表情報
     * @param productPowerParamMst
     * @return
     */

    boolean delSmartData(ProductPowerParamMst productPowerParamMst);

    /**
     * 編集，つかむ取商品里点数表参数
     */
     Map<String,Object> getAllDataOrParam(String companyCd,Integer productPowerNo,Integer isCover);


    Map<String, Object> getPrefectureAndChanelInfo();


}
