package com.trechina.planocycle.service;

import java.util.Map;

public interface ClassicPriorityOrderMstAttrSortService {

    /**
     * 既存のデータのソートの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityAttrSort(String companyCd,Integer priorityOrderCd);

    /**
     * 削除データのソート
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityAttrSortInfo(String companyCd,Integer priorityOrderCd);
}
