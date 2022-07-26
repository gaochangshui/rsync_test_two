package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MstJanParamMapper {
     List<Map<String, Object>> getAttributeTree(String companyCd, String classCd);
}
