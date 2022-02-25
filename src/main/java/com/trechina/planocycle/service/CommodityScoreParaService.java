package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.ProductPowerReserveMst;
import com.trechina.planocycle.entity.vo.ProductPowerPrimaryKeyVO;
import com.trechina.planocycle.entity.vo.RankCalculateVo;

import java.util.Map;

public interface CommodityScoreParaService {
    /**
     * 获取表示项目参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    Map<String,Object> getCommodityScorePara(String conpanyCd, Integer productPowerCd );

    /**
     * 保存表示项目参数,weight参数，step1期间参数
     * @param commodityScorePara
     * @return
     */
    Map<String,Object> setCommodityScorePare(ProductPowerParam productPowerParam);

    /**
     * 获取weight参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    Map<String,Object> getCommodityScoreWeight(String conpanyCd, Integer productPowerCd);

    /**
     * 获取表示项目的预备项目参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    Map<String,Object> getCommodityScorePrePara(String conpanyCd, Integer productPowerCd);

    /**
     * 删除商品力点数表所有信息
     * @param primaryKeyVO
     * @return
     */
    Map<String, Object> delCommodityScoreAllInfo(ProductPowerPrimaryKeyVO primaryKeyVO);

    /**
     *
     * @param productPowerReserveMst
     * @return
     */
    Map<String, Object> delYoBi(ProductPowerReserveMst productPowerReserveMst);

    /**
     * rank计算
     * @param rankCalculateVo
     * @return
     */
    Map<String,Object>  rankCalculate( RankCalculateVo rankCalculateVo);
}
