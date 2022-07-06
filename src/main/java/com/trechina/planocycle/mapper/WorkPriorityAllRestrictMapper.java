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

    List<Map<String, Object>> selectByPriorityAllCd(Integer priorityAllCd, Integer patternCd);
}
