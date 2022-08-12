package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BasicPatternJanPlacementMapper {

    List<Map<String,Object>> getJanPlacement();
}
