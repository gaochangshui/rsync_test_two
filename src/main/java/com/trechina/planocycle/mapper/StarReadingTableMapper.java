package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.StarReadingTableDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface StarReadingTableMapper {

    List<Map<String,Object>> getBranchdiff(@Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String,Object>> getBranchList(@Param("priorityOrderCd") Integer priorityOrderCd,@Param("groupCompany")List<String> groupCompany
    ,@Param("tableName")String tableName);

    List<Map<String,Object>> getAreaList(Integer priorityOrderCd,List<String> groupCompany,String tableName);

    List<Map<String,Object>> getPatternList(Integer priorityOrderCd);

    List<Map<String, Object>> getBranchdiffForBranch(@Param("item") StarReadingTableDto starReadingTableDto
            , @Param("branchList")List<Map<String,Object>> branchList,@Param("tableName")String tableName,@Param("groupCompany")List<String> groupCompany);

    List<Map<String,Object>> getPatterndiff(@Param("priorityOrderCd") Integer priorityOrderCd,@Param("tableName")String tableName);

    List<Map<String,Object>> getPatternNameList(@Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String,Object>> getPatterndiffForPattern(@Param("item") StarReadingTableDto starReadingTableDto,@Param("shelfNameCd")List<String> shelfNameCd);

    void setBranchList(List<Map<String,Object>> list,String companyCd,Integer priorityOrderCd);

    void delBranchList(String companyCd, Integer priorityOrderCd);

    void setPatternList(List<Map<String, Object>> list,String companyCd,Integer priorityOrderCd);

    void delPatternList(String companyCd, Integer priorityOrderCd);

    List<Map<String, Object>> selectJanForPattern(String companyCd, Integer priorityOrderCd, Integer shelfPatternCd);

    List<Map<String, Object>> selectJanForBranch(String companyCd, Integer priorityOrderCd, String branchList);

    List<String> selectBranchMustNotJan(String companyCd, Integer priorityOrderCd);

    void deleteFinalByBranch(String companyCd, Integer priorityOrderCd);

    void deleteFinalByPattern(String companyCd, Integer priorityOrderCd);

    int setFinalForWorkByBranch(String companyCd, Integer priorityOrderCd);

    int setFinalForWorkByPattern(String companyCd, Integer priorityOrderCd);

    void deleteWorkByPattern(String companyCd, Integer newPriorityOrderCd);

    void insertForFinalByPattern(String companyCd, Integer priorityOrderCd, Integer newPriorityOrderCd);

    void deleteWorkByBranch(String companyCd, Integer newPriorityOrderCd);

    void insertForFinalByBranch(String companyCd, Integer priorityOrderCd, Integer newPriorityOrderCd);

    List<Map<String,Object>> getJanOrName(String companyCd,Integer priorityOrderCd,String tableName);
}
