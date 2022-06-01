package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ShelfPtsDataTaimst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ShelfPtsDataTaimstMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd, @Param("taiCd") Integer taiCd);

    int insert(ShelfPtsDataTaimst record);

    int insertSelective(ShelfPtsDataTaimst record);

    ShelfPtsDataTaimst selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd, @Param("taiCd") Integer taiCd);

    List<ShelfPtsDataTaimst> selectByPtsCd(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd);

    int updateByPrimaryKeySelective(ShelfPtsDataTaimst record);

    int updateByPrimaryKey(ShelfPtsDataTaimst record);

    int insertAll(List<ShelfPtsDataTaimst> list);

    List<ShelfPtsDataTaimst> selectNewByPtsCd(String companyCd, Integer ptsCd);
}