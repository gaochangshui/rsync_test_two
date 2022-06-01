package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderSpace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderSpaceMapper {
    int insert(WorkPriorityOrderSpace record);

    int insertSelective(WorkPriorityOrderSpace record);

    int insertAll(List<WorkPriorityOrderSpace> list);

    int deleteByAuthorCd(String companyCd, String authorCd,Integer priorityOrderCd );

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd")String authorCd);

}