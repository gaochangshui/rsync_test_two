package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderMstMapper {
    int insert(WorkPriorityOrderMst record);

    int insertSelective(WorkPriorityOrderMst record);

    int deleteByAuthorCd(String companyCd, String authorCd,Integer priorityOrderCd);

    WorkPriorityOrderMst selectByAuthorCd(@Param("companyCd") String companyCd,@Param("authorCd") String authorCd,  @Param("priorityOrderCd") Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);

    List<WorkPriorityOrderMst> getWorkPriorityOrderMst(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
}