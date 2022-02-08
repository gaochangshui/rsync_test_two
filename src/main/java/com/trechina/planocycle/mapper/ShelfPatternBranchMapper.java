package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ShelfPatternBranch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ShelfPatternBranchMapper {
    int deleteByPrimaryKey(Integer shelfPatternCd);

    int insert(@Param("lists") List<ShelfPatternBranch> record);

    List<ShelfPatternBranch> selectByPrimaryKey(@Param("shelfPatternCd") Integer shelfPatternCd);

}
