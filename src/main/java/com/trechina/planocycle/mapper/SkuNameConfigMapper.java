package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;
@Mapper
public interface SkuNameConfigMapper {

    Integer getJanName2colNum(String companyCd,String paragraph);

    Integer getJanItem2colNum(String companyCd,String paragraph);

    Map<String,Object> getKokyakuShow(String companyCd, String paragraph);
}
