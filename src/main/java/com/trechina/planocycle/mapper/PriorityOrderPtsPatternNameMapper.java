package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderPtsPatternNameMapper {
    int insertPtsPatternName(Integer priorityOrderCd, Integer shelfNameCd, List<Map<String, Object>> newJanList);

    int deletePtsPatternName();

    List<String> selectExistPatternJan(Integer priorityOrderCd, Integer shelfNameCd, List<String> newPtsJanCdList);

    void deletePtsPatternNameByCd(Integer priorityOrderCd);
}
