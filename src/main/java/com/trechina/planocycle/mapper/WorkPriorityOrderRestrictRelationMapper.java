package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderRestrictRelationMapper {
    int insert(WorkPriorityOrderRestrictRelation record);

    int insertSelective(WorkPriorityOrderRestrictRelation record);

    int insertAll(List<WorkPriorityOrderRestrictRelation> list);

    int deleteByAuthorCd(String companyCd, String authorCd,Integer priorityOrderCd);

    List<WorkPriorityOrderRestrictRelation> selectByAuthorCd(String companyCd, String authorCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);

}