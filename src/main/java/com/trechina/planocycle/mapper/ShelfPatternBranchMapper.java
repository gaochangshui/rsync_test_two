package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ShelfPatternBranchDto;
import com.trechina.planocycle.entity.po.ShelfPatternBranch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ShelfPatternBranchMapper {
    int deleteByPrimaryKey(@Param("shelfPatternCd") Integer shelfPatternCd,@Param("authorCd") String authorCd);

    int insert(@Param("lists") List<ShelfPatternBranch> record,@Param("authorCd") String authorCd);

    List<ShelfPatternBranch> selectByPrimaryKey(@Param("shelfPatternCd") Integer shelfPatternCd);

    List<String> getShelfPatternBranch(@Param("id") Integer id);


    int deleteBranchCd (@Param("branchList")List<String> branchList, @Param("shelfPatternCd") Integer shelfPatternCd,@Param("authorCd")String authorCd);

    int deleteByPatternCd(@Param("shelfPatternCd") Integer shelfPatternCd);
    Integer setDelFlg(@Param("branch")String branch,@Param("shelfPatternCd") Integer shelfPatternCd,@Param("authorCd")String authorCd);

    List<ShelfPatternBranchDto> getBranch(@Param("companyCd")String companyCd);

    List<ShelfPatternBranch> getPatternBranch(@Param("list")List<Integer> shelfPatternCdList);

    List<Map<String, Object>> selectAllPatternBranch(@Param("priorityOrderCd") Integer priorityOrderCd, String companyCd,
                                                     Map<String, String> tenTableName, Integer shelfPatternCd);

}
