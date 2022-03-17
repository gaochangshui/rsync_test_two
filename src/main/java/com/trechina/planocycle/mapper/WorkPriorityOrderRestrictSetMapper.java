package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictSet;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkPriorityOrderRestrictSetMapper {
    int insert(WorkPriorityOrderRestrictSet record);

    int insertSelective(WorkPriorityOrderRestrictSet record);

    int insertAll(List<WorkPriorityOrderRestrictSet> list);

    int deleteByAuthorCd(String companyCd, String authorCd,Integer priorityOrderCd);

    List<WorkPriorityOrderRestrictSet> selectByAuthorCd(String companyCd, String authorCd,Integer priorityOrderCd);
}