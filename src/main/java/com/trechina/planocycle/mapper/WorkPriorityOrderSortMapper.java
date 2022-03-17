package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkPriorityOrderSortMapper {

    int delete(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);
}
