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

    /**
     *获取属性1和属性2
     */
    Map<String,Object> getAttribute();
    /**
     * 获取属性的分类及商品分类列表
     */
    Map<String,Object> getAttributeList();




    /**
     *获取属性1属性2组合对应的面积
     */
    Map<String,Object> getAttributeArea(Integer patternCd,Integer attr1,Integer attr2);
}
