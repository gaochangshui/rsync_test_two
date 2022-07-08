package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderPtsDataMapper {
    List<Map<String, Object>> selectTanaMstByPatternCd(Integer shelfPatternCd, Integer priorityOrderCd);

}
