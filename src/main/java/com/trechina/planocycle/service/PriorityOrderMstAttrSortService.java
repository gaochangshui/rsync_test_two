package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;

import java.util.List;
import java.util.Map;

public interface PriorityOrderMstAttrSortService {



    /**
     * 保存数据的排序
     *
     * @param priorityOrderMstAttrSort
     * @return
     */
    Map<String, Object> setPriorityAttrSort(List<PriorityOrderMstAttrSort> priorityOrderMstAttrSort);

    /**
     * 削除数据的排序
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityAttrSortInfo(String companyCd, Integer priorityOrderCd);

    /**
     * つかむ取属性1和属性2
     */
    Map<String, Object> getAttribute(PriorityOrderAttrDto priorityOrderAttrDto);

    /**
     * つかむ取属性的分クラス及商品分クラス列表
     */
    Map<String, Object> getAttributeList(PriorityOrderAttrDto priorityOrderAttrDto);



    Map<String, Object> getAttrGroup(PriorityOrderAttrDto priorityOrderAttrDto);
}
