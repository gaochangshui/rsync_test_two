package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.ShelfPtsDataJandata;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    List<ShelfPtsDataJandata> selectNewByPtsCd(String companyCd, Integer ptsCd);

    List<PriorityOrderResultDataDto> selectJanByPatternCd(String authorCd, String companyCd, Integer patternCd, Integer priorityOrderCd,
                                                          List<Map<String, Object>> attrHeaderList);
}
