package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ShelfPatternArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ShelfPatternAreaMapper {
    int deleteByPrimaryKey( @Param("shelfPatternCd") Integer shelfPatternCd,@Param("authorCd")String authorCd);

    int insert(@Param("lists") List<ShelfPatternArea> record,@Param("authorCd")String authorCd);

    List<Integer> getShelfPatternArea(@Param("id") Integer id,@Param("companyCd")String companyCd);

    int deleteAreaCd (@Param("areaCd")List<Integer> areaCd,@Param("shelfPatternCd") Integer shelfPatternCd,@Param("authorCd")String authorCd);

    Integer setDelFlg(@Param("areaCd")Integer areaCd,@Param("shelfPatternCd") Integer shelfPatternCd,@Param("authorCd")String authorCd);


}
