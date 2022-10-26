package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.SysConfigDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysConfigMapper {
    String selectSycConfig(String targetColumn);
    List<Map<String, String>> selectByPrefix(String targetColumnPre);

    Integer selectIsGroupCompany(String companyCd);

    List<Map<String,Object>> getAttrHeader(String tableName);

    int updateValByName(String name, Object value);

    void updateVal(List<SysConfigDto> janUnitList);
}
