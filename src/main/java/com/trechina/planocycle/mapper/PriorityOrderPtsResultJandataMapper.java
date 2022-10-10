package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderPtsResultJandataMapper {
    int insertPtsJandata(List<Map<String, Object>> newPtsJanList, String companyCd, Integer priorityOrderCd, Integer shelfPatternCd,
                         String branchCd, Integer compareFlag);

    void deletePtsJandata(Integer priorityOrderCd);

    List<Map<String, Object>> selectAllResultJandata(Integer priorityOrderCd);
}
