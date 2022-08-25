package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkuNameConfigMapper {

    Integer getJanName2colNum(String companyCd,String paragraph);

    Integer getJanItem2colNum(String companyCd,String paragraph);
}
