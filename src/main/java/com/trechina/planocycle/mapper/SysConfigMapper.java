package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysConfigMapper {
    String selectSycConfig(String targetColumn);
    List<Map<String, String>> selectByPrefix(String targetColumnPre);

    Integer selectIsGroupCompany(String companyCd);
}
