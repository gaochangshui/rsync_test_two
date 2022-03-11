package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkPriorityOrderRestrictRelationMapper {
    int insert(WorkPriorityOrderRestrictRelation record);

    int insertSelective(WorkPriorityOrderRestrictRelation record);
}