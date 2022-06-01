package com.trechina.planocycle.service;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ProductPowerMstService {
    /**
     * 企業cdによる商品力点数表一覧の取得
     * @param companyCd
     * @return
     */
    Map<String,Object> getTableName(String companyCd);
    /**
     * mst基本情報の取得
     * @param companyCd
     * @return
     */
    Map<String,Object> getProductPowerTable(String companyCd);

    /**
     * 商品力点数表一覧データの取得
     * @param companyCd
     * @param productPowerCd
     * @param priorityOrderCd
     * @return
     */
     Map<String,Object> getProductPowerInfo(String companyCd ,Integer productPowerCd,Integer priorityOrderCd);

    /**
     * 商品力点数表データを取得excel download
     * @param companyCd
     * @param productPowerCd
     * @param response
     */
    void downloadProductPowerInfo(String companyCd, Integer productPowerCd, HttpServletResponse response);

    Map<String, Object> getPatternForBranch(String companyCd,Integer productPowerCd);
}
