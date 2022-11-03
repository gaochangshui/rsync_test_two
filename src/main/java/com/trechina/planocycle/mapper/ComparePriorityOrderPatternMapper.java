package com.trechina.planocycle.mapper;


import com.trechina.planocycle.entity.po.ComparePriorityOrderPattern;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ComparePriorityOrderPatternMapper {
    void insertWK( @Param("list") List<ComparePriorityOrderPattern> comparePriorityOrderPattern);
    void setWKForFinal( Integer priorityOrderCd);
    void setFinalForWK( Integer priorityOrderCd);

    void  delFinal(Integer priorityOrderCd);
    void  delWk(Integer priorityOrderCd);
}
