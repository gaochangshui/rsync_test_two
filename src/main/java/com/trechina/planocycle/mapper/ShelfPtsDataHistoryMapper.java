package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ShelfPtsDataHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface ShelfPtsDataHistoryMapper {
    int deleteByPrimaryKey(@Param("ptsCd") Integer ptsCd, @Param("patternCd") Integer patternCd, @Param("companyCd") String companyCd);

    int insert(ShelfPtsDataHistory record);

    int insertSelective(ShelfPtsDataHistory record);

    ShelfPtsDataHistory selectByPrimaryKey(@Param("ptsCd") Integer ptsCd, @Param("patternCd") Integer patternCd, @Param("companyCd") String companyCd);

    int updateByPrimaryKeySelective(ShelfPtsDataHistory record);

    int updateByPrimaryKey(ShelfPtsDataHistory record);
}