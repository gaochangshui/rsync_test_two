package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PriorityOrderRestrictSetMapper {
    int setPriorityOrderRestrict(@Param("item") PriorityOrderRestrictSet priorityOrderRestrictSet,@Param("authorCd") String authorCd);
}
