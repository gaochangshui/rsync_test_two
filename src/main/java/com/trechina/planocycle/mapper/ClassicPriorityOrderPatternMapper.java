package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ShelfPtsDataDto;
import com.trechina.planocycle.entity.po.PriorityOrderPattern;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderPatternMapper {
    int deleteByPrimaryKey(@Param("priorityOrderCd") Integer priorityOrderCd, @Param("companyCd") String companyCd, @Param("shelfPatternCd") Integer shelfPatternCd);

    int insert(@Param("lists") List<PriorityOrderPattern> record);

    int selectByPriorityOrderName(String companyCd,String priorityOrderName,Integer priorityOrderCd);

    int deleteforid(Integer priorityOrderCd);

    List<ShelfPtsDataDto> selectPattern(String companyCd, Integer priorityOrderCd);

    String getShelfName(Integer priorityOrderCd,String companyCd);

    int deleteWork(Integer priorityOrderCd);

    int insertWork(@Param("lists") List<PriorityOrderPattern> record);

    void insertWorkForFinal(String companyCd, Integer priorityOrderCd, Integer newPriorityOrderCd);

    List<Map<String,Object>> getShelfNameCd(String companyCd, String[] shelfPatternList);

}
