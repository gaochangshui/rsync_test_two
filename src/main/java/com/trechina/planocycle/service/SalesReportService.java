package com.trechina.planocycle.service;

import java.util.Map;

public interface SalesReportService {

    /**
     * 获取棚别实际数据
     * @param taskID
     * @return
     */
    Map<String, Object> getShelfReportInfo(String taskID);

    /**
     * 获取商品别实际数据
     * @param taskID
     * @return
     */
    Map<String, Object> getProductReportInfo(String taskID);

    /**
     * 获取单jan别实际数据
     * @param taskID
     * @return
     */
    Map<String, Object> getJanReportDetailInfo(String taskID,String startDay,String endDay);

}
