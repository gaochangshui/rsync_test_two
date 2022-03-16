package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderResultData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderSortRankMapper {

    int delete(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

    int insert(@Param("companyCd")String companyCd, @Param("list")List<WorkPriorityOrderResultData> list, @Param("authorCd")String authorCd);
}
