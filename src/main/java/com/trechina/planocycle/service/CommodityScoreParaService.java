package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.vo.ProductPowerPrimaryKeyVO;

import java.util.List;
import java.util.Map;

public interface CommodityScoreParaService {

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
     * プロジェクトのアップロードをデータベースに保存する
     * @param data
     * @param companyCd
     * @param productPowerCd
     * @param dataName
     * @param valueCd
     * @return
     */
    Map<String, Object> saveYoBi(List<String[]> data, String companyCd, Integer productPowerCd, String dataName, Integer valueCd);
    /**
     * rank計算
     * @param
     * @return
     */
    Map<String,Object>  rankCalculate( Map<String,Object> map);

    /**
     * reserveの削除
     * @return
     */
     Map<String,Object> deleteReserve(JSONObject jsonObject);

    Map<String, Object> rankCalculateForTaskId(String taskID);
}
