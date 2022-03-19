package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderRestrictSetMapper {
    int insert(WorkPriorityOrderRestrictSet record);

    int insertSelective(WorkPriorityOrderRestrictSet record);

    int insertAll(List<WorkPriorityOrderRestrictSet> list);

    int deleteByAuthorCd(String companyCd, String authorCd,Integer priorityOrderCd);

    List<WorkPriorityOrderRestrictSet> selectByAuthorCd(String companyCd, String authorCd,Integer priorityOrderCd);
    List<PriorityOrderRestrictSet> getWorkPrioritySet(String companyCd, String authorCd, Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd")String authorCd);

}