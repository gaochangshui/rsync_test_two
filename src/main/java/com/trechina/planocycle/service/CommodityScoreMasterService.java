package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.ProductCdAndNameDto;
import com.trechina.planocycle.entity.po.ProductPowerParamMst;
import com.trechina.planocycle.entity.vo.ProductOrderAttrAndItemVO;

import java.util.Map;

public interface CommodityScoreMasterService {
    /**
     * 获取企业信息接口
     * @return
     */
    Map<String,Object> getEnterpriseInfo();

    /**
     * 获取企业cd关联的商品力点数List接口
     * @param conpanyCd
     * @return
     */
    Map<String,Object> getCommodityListInfo(String conpanyCd);

    /**
     * 获取商品力点数的参数接口
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    Map<String,Object> getCommodityParam(String conpanyCd,Integer productPowerCd);

    /**
     * 保存商品力地拿鼠标的模板名接口
     * @param productPowerName
     * @return
     */
    Map<String,Object> setCommodityList(ProductCdAndNameDto productPowerName);



    /**
     * 获取Chanel信息接口
     * @return
     */
    Map<String, Object> getChanelInfo();


    void productPowerParamAttr(String conpanyCd, Integer productPowerCd, Map<String, Object> result);

    void productPowerParamAttrName(String conpanyCd, Integer productPowerCd, Map<String, Object> result);

    /**
     * 获取商品力点数表的属性和品目信息，用于优先顺位表
     * @param productOrderCd
     * @return
     */
    ProductOrderAttrAndItemVO getAttrAndItmemInfo(String companyCd, Integer productOrderCd);

    /**
     * 削除商品力点数表信息
     * @param productPowerParamMst
     * @return
     */

    boolean delSmartData(ProductPowerParamMst productPowerParamMst);

    /**
     * 编辑时，获取商品里点数表参数
     */
    public Map<String,Object> getAllDataOrParam(String companyCd,Integer productPowerNo);
}
