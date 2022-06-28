package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkPriorityOrderJanCutMapper {
    List<Map<String, Object>> selectJanCut(Integer priorityOrderCd);
}
