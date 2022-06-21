package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.Zokusei;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ZokuseiMstMapper {
    int insertBatch(@Param("lists") List<Zokusei> lists);

    List<Map<String, Object>> selectHeader(String tableName);

    List<String> selectAllAttrTable(String schema);

    List<String> selectAllKaisouTable(String schema);
}
