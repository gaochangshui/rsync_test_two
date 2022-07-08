package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityAllRestrictDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkPriorityAllRestrictMapper {


    Integer insertWKTableRestrict(@Param("allRestrictDtoList") List<PriorityAllRestrictDto> allRestrictDtoList);

    Integer deleteWKTableRestrict(@Param("companyCd")String companyCd, @Param("priorityAllCd")Integer priorityAllCd);

    Integer setBasicPatternResult(String companyCd, Integer priorityOrderCd, Integer shelfPatternCd, Integer priorityAllCd, String authorCd);

    Integer deleteBasicPatternResult(String companyCd,Integer priorityAllCd,String authorCd,Integer patternCd);

    List<Map<String, Object>> selectByPriorityAllCd(Integer priorityAllCd, Integer patternCd);
}
