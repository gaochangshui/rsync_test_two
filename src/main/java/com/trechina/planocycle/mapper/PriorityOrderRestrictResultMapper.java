package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderRestrictResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PriorityOrderRestrictResultMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(PriorityOrderRestrictResult record);

    int insertSelective(PriorityOrderRestrictResult record);

    PriorityOrderRestrictResult selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int updateByPrimaryKeySelective(PriorityOrderRestrictResult record);

    int updateByPrimaryKey(PriorityOrderRestrictResult record);

    int insertBySelect(@Param("companyCd") String companyCd,@Param("authorCd") String authorCd,@Param("priorityOrderCd") Integer priorityOrderCd);

    int logicDeleteByPriorityOrderCd(@Param("companyCd") String companyCd,@Param("authorCd") String authorCd,@Param("priorityOrderCd") Integer priorityOrderCd);
}