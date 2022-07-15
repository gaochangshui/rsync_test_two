package com.trechina.planocycle.mapper;


import com.trechina.planocycle.entity.po.ShelfPtsData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkPriorityOrderPtsClassify {


    void setWorkPtsClassify(String companyCd,Integer priorityOrderCd, List<ShelfPtsData> shelfPtsData,List<String> colNameList);

    void deleteWork(String companyCd, Integer priorityOrderCd);

}
