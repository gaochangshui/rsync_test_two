package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderRestrictResultMapper {
    int insert(WorkPriorityOrderRestrictResult record);

    int insertSelective(WorkPriorityOrderRestrictResult record);

    int insertAll(List<WorkPriorityOrderRestrictResult> list);

    List<WorkPriorityOrderRestrictResult> getResultList(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);
}