package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.BasicPatternAttrListDto;
import com.trechina.planocycle.entity.po.BasicPatternRestrictResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BasicPatternAttrMapper {

    List<BasicPatternAttrListDto> getAttribute(String prodIsCore, String prodMstClass);

    void delete(Integer priorityOrderCd, String companyCd);

    int insertBatch(List<BasicPatternRestrictResult> lists);

    void deleteFinal(String companyCd, String authorCd, Integer priorityOrderCd);

    void setFinalForWork(String companyCd, String authorCd, Integer priorityOrderCd);

    void setWorkForFinal(String companyCd, Integer priorityOrderCd,Integer newPriorityOrderCd);
}
