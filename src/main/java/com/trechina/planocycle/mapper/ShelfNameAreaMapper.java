package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ShelfNameArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelfNameAreaMapper {
    int deleteByPrimaryKey(@Param("shelfNameCd") Integer shelfNameCd);

    int insert(@Param("lists") List<ShelfNameArea> record);

}
