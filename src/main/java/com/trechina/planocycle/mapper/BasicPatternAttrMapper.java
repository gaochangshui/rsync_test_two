package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.BasicPatternAttrListDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BasicPatternAttrMapper {

    List<BasicPatternAttrListDto> getAttribute(String prodIsCore, String prodMstClass);
}
