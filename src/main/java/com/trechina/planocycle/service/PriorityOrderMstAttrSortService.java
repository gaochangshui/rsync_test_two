package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;

import java.util.List;
import java.util.Map;

public interface PriorityOrderMstAttrSortService {

    /**
     * 获取既存数据的排序
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityAttrSort(String companyCd,Integer priorityOrderCd);


    /**
     * 保存数据的排序
     * @param priorityOrderMstAttrSort
     * @return
     */
    Map<String,Object> setPriorityAttrSort(List<PriorityOrderMstAttrSort> priorityOrderMstAttrSort);

    /**
     * 删除数据的排序
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityAttrSortInfo(String companyCd,Integer priorityOrderCd);
}
