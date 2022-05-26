package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONArray;

import java.util.Map;

public interface ClassicPriorityOrderCatePakService {
    /**
     * 获取カテパケ拡縮
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderCatePak(String companyCd,Integer priorityOrderCd);

    /**
     * 保存カテパケ拡縮
     * @param jsonArray
     * @return
     */
    Map<String,Object> setPriorityOrderCatePak(JSONArray jsonArray);

    /**
     * 削除カテパケ拡縮
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderCatePakInfo(String companyCd,Integer priorityOrderCd);

    /**
     * 削除カテパケ拡縮属性
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderCatePakAttrInfo(String companyCd,Integer priorityOrderCd);
}
