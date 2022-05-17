package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONArray;

import java.util.Map;

public interface ClassicPriorityOrderJanNewService {
    /**
     * 获取新规janlist
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanNew(String companyCd,Integer priorityOrderCd, Integer productPowerNo);

    /**
     *保存新规商品list
     * @param jsonArray
     * @return
     */
    Map<String,Object> setPriorityOrderJanNew(JSONArray jsonArray);

    /**
     * 删除新规商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delriorityOrderJanNewInfo(String companyCd,Integer priorityOrderCd);
}
