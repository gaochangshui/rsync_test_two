package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONArray;

import java.util.Map;

public interface ClassicPriorityOrderJanNewService {
    /**
     * 新規商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanNew(String companyCd,Integer priorityOrderCd);

    /**
     *新規商品リストの保存
     * @param jsonArray
     * @return
     */
    Map<String,Object> setPriorityOrderJanNew(JSONArray jsonArray);

    /**
     * 削除新规商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delriorityOrderJanNewInfo(String companyCd,Integer priorityOrderCd);
}
