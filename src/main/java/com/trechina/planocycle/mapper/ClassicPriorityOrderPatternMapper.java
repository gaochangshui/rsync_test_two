package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderPattern;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassicPriorityOrderPatternMapper {
    int deleteByPrimaryKey(@Param("priorityOrderCd") Integer priorityOrderCd, @Param("companyCd") String companyCd, @Param("shelfPatternCd") Integer shelfPatternCd);

    int insert(@Param("lists") List<PriorityOrderPattern> record);

    int selectByPriorityOrderName(String companyCd,String priorityOrderName,Integer priorityOrderCd,String authorCd);

    int deleteforid(Integer priorityOrderCd);
}
