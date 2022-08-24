package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysConfigMapper {
    String selectSycConfig(String targetColumn);
    List<String> selectByPrefix(String targetColumnPre);
}
