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
     * 削除新規商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delriorityOrderJanNewInfo(String companyCd,Integer priorityOrderCd);

    /**
     * 分類によって商品の力点数表を除いて同類の商品を抽出する
     * @param
     * @return
     */
    Map<String, Object> getSimilarity(Map<String,Object> map);
}
