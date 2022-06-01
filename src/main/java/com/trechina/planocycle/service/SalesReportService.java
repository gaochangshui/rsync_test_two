package com.trechina.planocycle.service;

import java.util.Map;

public interface SalesReportService {

    /**
     * 棚別実績データの取得
     * @param taskID
     * @return
     */
    Map<String, Object> getShelfReportInfo(String taskID);

    /**
     * 商品別実績データの取得
     * @param taskID
     * @return
     */
    Map<String, Object> getProductReportInfo(String taskID);

    /**
     * 単一janの他のデータの取得
     * @param taskID
     * @return
     */
    Map<String, Object> getJanReportDetailInfo(String taskID,String startDay,String endDay);

}
