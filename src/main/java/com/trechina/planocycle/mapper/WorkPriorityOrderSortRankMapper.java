package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkPriorityOrderSortRankMapper {

    int delete(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);
}
