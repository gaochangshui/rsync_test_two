package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.StarReadingTableDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface StarReadingTableMapper {

    List<Map<String,Object>> getBranchdiff(@Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String,Object>> getBranchList(@Param("priorityOrderCd") Integer priorityOrderCd,@Param("companyCd")String companyCd);

    List<Map<String,Object>> getAreaList(Integer priorityOrderCd);

    List<Map<String,Object>> getPatternList(Integer priorityOrderCd);

    List<Map<String, Object>> getBranchdiffForBranch(@Param("item") StarReadingTableDto starReadingTableDto
            , @Param("branchList")List<Map<String,Object>> branchList);

    List<Map<String,Object>> getPatterndiff(@Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String,Object>> getPatternNameList(@Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String,Object>> getPatterndiffForPattern(@Param("item") StarReadingTableDto starReadingTableDto,@Param("shelfNameCd")List<String> shelfNameCd);

    void setBranchList(List<Map<String,Object>> list);

    void delBranchList(String companyCd, Integer priorityOrderCd);

    void setPatternList(List<Map<String, Object>> list);

    void delPatternList(String companyCd, Integer priorityOrderCd);

    List<Map<String, Object>> selectJanForPattern(String companyCd, Integer priorityOrderCd, Integer shelfPatternCd);

    List<Map<String, Object>> selectJanForBranch(String companyCd, Integer priorityOrderCd, String branchList);

    int selectCountMustNotJan(String companyCd, Integer priorityOrderCd);

}
