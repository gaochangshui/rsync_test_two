package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.BasicPatternAttrListDto;
import com.trechina.planocycle.entity.po.BasicPatternRestrictResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BasicPatternAttrMapper {

    List<BasicPatternAttrListDto> getAttribute(String prodIsCore, String prodMstClass);

    void delete(Integer priorityOrderCd, String companyCd);

    int insertBatch(List<BasicPatternRestrictResult> lists);
}
