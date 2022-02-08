package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.Areas;
import org.apache.ibatis.annotations.Mapper;

import java.awt.geom.Area;
import java.util.List;
@Mapper
public interface AreasMapper {

    int delete();

    List<Areas> select(String companyCd);

    List<Areas> selectForShelfName(Integer ShelfNameCd);

    int updateTransfer();
}
