package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderRestrictResult;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderRestrictResultMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int deleteByAuthorCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(PriorityOrderRestrictResult record);

    int insertSelective(PriorityOrderRestrictResult record);

    PriorityOrderRestrictResult selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int updateByPrimaryKeySelective(PriorityOrderRestrictResult record);

    int updateByPrimaryKey(PriorityOrderRestrictResult record);

    int insertBySelect(@Param("companyCd") String companyCd,@Param("authorCd") String authorCd,@Param("priorityOrderCd") Integer priorityOrderCd);

    int logicDeleteByPriorityOrderCd(@Param("companyCd") String companyCd,@Param("authorCd") String authorCd,@Param("priorityOrderCd") Integer priorityOrderCd);

    List<WorkPriorityOrderRestrictResult> getPriorityOrderRestrictAll(@Param("companyCd")String companyCd, @Param("priorityOrderCd")Integer priorityOrderCd);

}