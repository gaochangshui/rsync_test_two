package com.trechina.planocycle.service;

import java.util.Map;

public interface CommodityScoreDataService {

    /**
     * 商品力点数表の基本データの取得
     *
     * @param
     * @return
     */
    Map<String, Object> getCommodityScoreData(Map<String,Object> map);

    /**
     * 商品力点数表基本taskidを取得する
     *
     * @param
     * @return
     */
    Map<String, Object> getCommodityScoreTaskId( Map<String,Object> map);


    Map<String, Object> getCommodityScoreDataFromDB(Integer productPowerCd, String companyCd, String[] posCd, String[] prepareCd, String[] intageCd,
                                                    String[] customerCd);
}
