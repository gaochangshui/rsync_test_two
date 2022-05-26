package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;

import java.util.List;
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
     * 保存データのソート
     * @param priorityOrderMstAttrSort
     * @return
     */
    Map<String,Object> setPriorityAttrSort(List<PriorityOrderMstAttrSort> priorityOrderMstAttrSort);

    /**
     * 削除データのソート
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityAttrSortInfo(String companyCd,Integer priorityOrderCd);
}
