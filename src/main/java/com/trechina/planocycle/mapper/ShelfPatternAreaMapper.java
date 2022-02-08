package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ShelfPatternArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ShelfPatternAreaMapper {
    int deleteByPrimaryKey( @Param("shelfPatternCd") Integer shelfPatternCd);

    int insert(@Param("lists") List<ShelfPatternArea> record);

}
