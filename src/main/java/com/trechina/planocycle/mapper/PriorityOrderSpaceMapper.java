package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderSpace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PriorityOrderSpaceMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(PriorityOrderSpace record);

    int insertSelective(PriorityOrderSpace record);

    PriorityOrderSpace selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int updateByPrimaryKeySelective(PriorityOrderSpace record);

    int updateByPrimaryKey(PriorityOrderSpace record);
}