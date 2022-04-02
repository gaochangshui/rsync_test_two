package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.ProductCdAndNameDto;
import com.trechina.planocycle.entity.po.ProductPowerParamMst;
import com.trechina.planocycle.entity.vo.ProductOrderAttrAndItemVO;

import java.util.Map;

public interface CommodityScoreMasterService {
    /**
     * 取得企業情報
     * @return
     */
    Map<String,Object> getEnterpriseInfo();

    /**
     * 取得企業cd关联の商品力点数List
     * @param conpanyCd
     * @return
     */
    Map<String,Object> getCommodityListInfo(String conpanyCd);

    /**
     * 取得商品力点数の参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    Map<String,Object> getCommodityParam(String conpanyCd,Integer productPowerCd);

    /**
     * 保存商品力地拿鼠标の模板名
     * @param productPowerName
     * @return
     */
    Map<String,Object> setCommodityList(ProductCdAndNameDto productPowerName);



    /**
     * 取得Chanel情報
     * @return
     */
    Map<String, Object> getChanelInfo();


    void productPowerParamAttr(String conpanyCd, Integer productPowerCd, Map<String, Object> result);

    void productPowerParamAttrName(String conpanyCd, Integer productPowerCd, Map<String, Object> result);

    /**
     * 商品力点数表の属性と品目情報を取得し、優先順位表に使用する
     * @param productOrderCd
     * @return
     */
    ProductOrderAttrAndItemVO getAttrAndItmemInfo(String companyCd, Integer productOrderCd);

    /**
     * 削除商品力点数表情報
     * @param productPowerParamMst
     * @return
     */

    boolean delSmartData(ProductPowerParamMst productPowerParamMst);

    /**
     * 編集，取得商品力点数表参数
     */
    public Map<String,Object> getAllDataOrParam(String companyCd,Integer productPowerNo);
}
