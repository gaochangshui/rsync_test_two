package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ShelfPtsDataVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface ShelfPtsDataVersionMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd);

    int insert(ShelfPtsDataVersion record);

    int insertSelective(ShelfPtsDataVersion record);

    ShelfPtsDataVersion selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd);

    int updateByPrimaryKeySelective(ShelfPtsDataVersion record);

    int updateByPrimaryKey(ShelfPtsDataVersion record);

    ShelfPtsDataVersion selectNewByPtsCd(String companyCd, Integer ptsCd);
}