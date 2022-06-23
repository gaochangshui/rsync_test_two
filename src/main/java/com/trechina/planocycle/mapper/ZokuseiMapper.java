package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ZokuseiMst;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ZokuseiMapper {
    List<ZokuseiMst> selectZokusei(String companyCd, String classCd, String zokuseiIds);

    List<Integer> selectCdHeader(String tableName, String attrTableName);
}
