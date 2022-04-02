package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.ProductPowerReserveMst;
import com.trechina.planocycle.entity.vo.ProductPowerPrimaryKeyVO;
import com.trechina.planocycle.entity.vo.RankCalculateVo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface CommodityScoreParaService {
    /**
     * 表示項目パラメータの取得
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    Map<String,Object> getCommodityScorePara(String conpanyCd, Integer productPowerCd );

    /**
     * 表示項目パラメータの保存,Weightパラメータ，Step 1期間パラメータ
     * @return
     */
    Map<String,Object> setCommodityScorePare(ProductPowerParam productPowerParam);





    /**
     * 商品力点表のすべての情報を削除
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

    Map<String, Object> saveYoBi(List<String[]> data, String companyCd, String dataCd,String dataName,Integer valueCd);
    /**
     * rank計算
     * @param rankCalculateVo
     * @return
     */
    Map<String,Object>  rankCalculate( RankCalculateVo rankCalculateVo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     * reserveの削除
     * @return
     */
     Map<String,Object> deleteReserve(JSONObject jsonObject);
}
