package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ShelfPtsDataTanaCount;
import com.trechina.planocycle.entity.po.ShelfPtsDataTaimst;
import com.trechina.planocycle.entity.po.ShelfPtsDataTanamst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelfPtsDataTanamstMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd, @Param("taiCd") Integer taiCd, @Param("tanaCd") Integer tanaCd);

    int insert(ShelfPtsDataTanamst record);

    int insertSelective(ShelfPtsDataTanamst record);

    ShelfPtsDataTanamst selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("ptsCd") Integer ptsCd, @Param("taiCd") Integer taiCd, @Param("tanaCd") Integer tanaCd);

    int updateByPrimaryKeySelective(ShelfPtsDataTanamst record);

    int updateByPrimaryKey(ShelfPtsDataTanamst record);

    int insertAll(List<ShelfPtsDataTanamst> list);

    List<ShelfPtsDataTanaCount> ptsTanaCountByTai(Long shelfPatternCd);

    List<ShelfPtsDataTanamst> selectByPatternCd(Long shelfPatternCd);

    List<ShelfPtsDataTanamst> selectByPtsCd(String companyCd, Integer ptsCd);
}