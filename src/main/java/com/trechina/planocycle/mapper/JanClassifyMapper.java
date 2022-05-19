package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * prod_0001_jan_kaisou_header_sys
 */
@Mapper
public interface JanClassifyMapper {
    List<Map<String, Object>> selectJanClassify(String tableName);
    List<Map<String, Object>> getJanClassify(String tableName,String tableNameAttr,Integer colNum);
}
