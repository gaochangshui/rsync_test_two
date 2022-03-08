package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderRestrictRelation;
import org.apache.ibatis.annotations.Param;

public interface PriorityOrderRestrictRelationMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(PriorityOrderRestrictRelation record);

    int insertSelective(PriorityOrderRestrictRelation record);

    PriorityOrderRestrictRelation selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int updateByPrimaryKeySelective(PriorityOrderRestrictRelation record);

    int updateByPrimaryKey(PriorityOrderRestrictRelation record);
}