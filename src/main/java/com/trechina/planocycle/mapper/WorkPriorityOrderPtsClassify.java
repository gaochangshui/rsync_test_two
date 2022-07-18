package com.trechina.planocycle.mapper;


import com.trechina.planocycle.entity.po.ShelfPtsData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkPriorityOrderPtsClassify {


    void setWorkPtsClassify(String companyCd,Integer priorityOrderCd, List<ShelfPtsData> shelfPtsData,List<String> colNameList);

    void deleteWork(String companyCd, Integer priorityOrderCd);

    List<Integer> getJanPtsCd(String companyCd, Integer priorityOrderCd, Map<String, Object> map);

    Integer getJanBranchNum(List<Integer> ptsCd, Map<String, Object> map);

    List<Map<String, Object>> getAttrInfo(String companyCd, Integer priorityOrderCd);
}
