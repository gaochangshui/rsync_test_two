package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderBranchNum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface PriorityOrderBranchNumMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(PriorityOrderBranchNum record);

    int insertSelective(PriorityOrderBranchNum record);

    PriorityOrderBranchNum selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("branch") Integer branch, @Param("janNew") String janNew);

    int updateByPrimaryKeySelective(PriorityOrderBranchNum record);

    int updateByPrimaryKey(PriorityOrderBranchNum record);
}
