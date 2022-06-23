package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface BasicPatternResultMapper {

    List<Map<String,Object>> getAttrComposeList(String companyCd, Integer priorityOrderCd,List<String> attrList,String classCd,String company);

    List<Map<String,Object>> getAttrHeaderName(List<String> attrList, String classCd,String companyCd);
}
