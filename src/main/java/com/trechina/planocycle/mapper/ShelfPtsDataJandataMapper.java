package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ShelfPtsDataJandata;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ShelfPtsDataJandataMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd, @Param("taiCd") Integer taiCd, @Param("tanaCd") Integer tanaCd, @Param("tanapositionCd") Integer tanapositionCd, @Param("jan") Integer jan);

    int insert(ShelfPtsDataJandata record);

    int insertSelective(ShelfPtsDataJandata record);

    ShelfPtsDataJandata selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd, @Param("taiCd") Integer taiCd, @Param("tanaCd") Integer tanaCd, @Param("tanapositionCd") Integer tanapositionCd, @Param("jan") Integer jan);

    int updateByPrimaryKeySelective(ShelfPtsDataJandata record);

    int updateByPrimaryKey(ShelfPtsDataJandata record);

    int insertAll(List<ShelfPtsDataJandata> list);

    List<ShelfPtsDataJandata> selectByPtsCd(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd);
}
