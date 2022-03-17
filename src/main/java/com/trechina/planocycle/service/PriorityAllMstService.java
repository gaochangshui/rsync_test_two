package com.trechina.planocycle.service;

import java.util.Map;

public interface PriorityAllMstService {

    /**
     * 自动计算
     * @return
     */
    Map<String, Object> autoCalculation(String companyCd,Integer priorityOrderCd);

    /**
     * 自动计算
     * @return
     */
    Map<String, Object> savePriorityAll(String companyCd,Integer priorityOrderCd);

}
