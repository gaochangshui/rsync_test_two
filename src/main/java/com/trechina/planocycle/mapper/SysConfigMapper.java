package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.SysConfigDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysConfigMapper {
    String selectSycConfig(String targetColumn);
    List<Map<String, String>> selectByPrefix(String targetColumnPre);

    List<Map<String, Object>> selectAllByPrefix(String targetColumnPre);

    Integer selectIsGroupCompany(String companyCd);

    List<Map<String,Object>> getAttrHeader(String tableName);

    int updateValByName(String name, Object value);

    void updateVal(List<Map<String, Object>> janUnitList);
}
