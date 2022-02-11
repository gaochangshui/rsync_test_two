package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ShelfNameArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelfNameAreaMapper {
    int deleteByPrimaryKey(@Param("shelfNameCd") Integer shelfNameCd,@Param("authorCd")String authorCd);

    int insert(@Param("lists") List<ShelfNameArea> record,@Param("authorCd") String authorCd);

    List<Integer> getShelfNameArea(@Param("id") Integer id,@Param("companyCd")String companyCd);

    int deleteAreaCd (@Param("areaCd")Integer areaCd,@Param("shelfNameCd") Integer shelfNameCd,@Param("authorCd")String authorCd);

    //恢复删除数据
    Integer setDelFlg(@Param("areaCd")Integer areaCd,@Param("shelfNameCd") Integer shelfNameCd,@Param("authorCd")String authorCd);
}
