package com.trechina.planocycle.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PlanocycleKigyoListMapper {

    List<Map<String,Object>> getCompanyList(List<String> companyCd,String authorCd);

    String getGroupInfo(String companyCd);
}
