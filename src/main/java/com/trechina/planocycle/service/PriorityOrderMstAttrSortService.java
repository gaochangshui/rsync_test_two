package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
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
     * つかむ取属性1和属性2
     */
    Map<String, Object> getAttribute(PriorityOrderAttrDto priorityOrderAttrDto);
    /**
     * 陳列設定つかむ取属性1和属性2
     */
    Map<String, Object> getAttributeSort();

    /**
     * つかむ取属性的分クラス及商品分クラス列表
     */
    Map<String, Object> getAttributeList();


    /**
     * 計算属性1属性2組合対応的面積
     */
    Map<String, Object> getAttributeArea(Integer patternCd, Integer attr1, Integer attr2);

    /**
     *編集つかむ取属性1属性2組合対応的面積
     */
    Map<String,Object> getEditAttributeArea(String companyCd);

    List<WorkPriorityOrderRestrictSet> setRestrict(List<PriorityOrderAttrVO> dataList, List<ShelfPtsDataTanamst> ptsDataTanamstList, List<ShelfPtsDataTanaCount> tanaCountList, Short attr1, Short attr2, String companyCd, String authorCd, Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    Map<String, Object> setAttribute(PriorityOrderSpaceDto dto);

    /**
     *
     */
    GetCommonPartsDataDto getCommonTableName(PriorityOrderAttrDto priorityOrderAttrDto);
}
