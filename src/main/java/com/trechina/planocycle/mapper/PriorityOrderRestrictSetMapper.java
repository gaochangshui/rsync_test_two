package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PriorityOrderRestrictSetMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,
            @Param("taiCd") Integer taiCd, @Param("tanaCd") Integer tanaCd,
            @Param("restrictType") Integer restrictType);

    int insert(PriorityOrderRestrictSet record);

    int insertSelective(PriorityOrderRestrictSet record);

    PriorityOrderRestrictSet selectByPrimaryKey(@Param("companyCd") String companyCd,
            @Param("priorityOrderCd") Integer priorityOrderCd, @Param("taiCd") Integer taiCd,
            @Param("tanaCd") Integer tanaCd, @Param("restrictType") Integer restrictType);

    int updateByPrimaryKeySelective(PriorityOrderRestrictSet record);

    int updateByPrimaryKey(PriorityOrderRestrictSet record);

    int setPriorityOrderRestrict(@Param("item") PriorityOrderRestrictSet priorityOrderRestrictSet,@Param("authorCd") String authorCd);
}
