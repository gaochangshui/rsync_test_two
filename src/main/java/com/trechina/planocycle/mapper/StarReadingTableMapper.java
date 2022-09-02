package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.StarReadingTableDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface StarReadingTableMapper {

    List<Map<String,Object>> getBranchdiff(@Param("item") StarReadingTableDto starReadingTableDto
            ,@Param("branchList")List<Map<String,Object>> branchList);

    List<Map<String,Object>> getBranchList(StarReadingTableDto starReadingTableDto);

    List<Map<String,Object>> getAreaList(Integer priorityOrderCd);

    List<Map<String,Object>> getPatternList(Integer priorityOrderCd);

    List<Map<String, Object>> getBranchdiffForBranch(@Param("item") StarReadingTableDto starReadingTableDto
            , @Param("branchList")List<Map<String,Object>> branchList);
}
