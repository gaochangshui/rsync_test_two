package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderPtsBackupJanMapper {
    int insertBackupJan(Integer priorityOrderCd, Integer shelfPatternCd, String group, String branch, List<Map<String, Object>> list);

    int deleteBackupJan();

    List<Map<String, Object>> selectBackupJan(Integer priorityOrderCd, Integer shelfPatternCd, Integer shelfNameCd,String branch);
}
