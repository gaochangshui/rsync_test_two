package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderSpaceDto;
import com.trechina.planocycle.entity.dto.ShelfPtsDataTanaCount;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.po.ShelfPtsDataTanamst;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictSet;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrVO;

import java.lang.reflect.InvocationTargetException;
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
     * 获取属性1和属性2
     */
    Map<String, Object> getAttribute();
    /**
     * 陈列设定获取属性1和属性2
     */
    Map<String, Object> getAttributeSort();

    /**
     * 获取属性的分类及商品分类列表
     */
    Map<String, Object> getAttributeList();


    /**
     * 计算属性1属性2组合对应的面积
     */
    Map<String, Object> getAttributeArea(Integer patternCd, Integer attr1, Integer attr2);

    /**
     *编辑时获取属性1属性2组合对应的面积
     */
    Map<String,Object> getEditAttributeArea(String companyCd);

    List<WorkPriorityOrderRestrictSet> setRestrict(List<PriorityOrderAttrVO> dataList, List<ShelfPtsDataTanamst> ptsDataTanamstList, List<ShelfPtsDataTanaCount> tanaCountList, Short attr1, Short attr2, String companyCd, String authorCd, Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    Map<String, Object> setAttribute(PriorityOrderSpaceDto dto);
}
