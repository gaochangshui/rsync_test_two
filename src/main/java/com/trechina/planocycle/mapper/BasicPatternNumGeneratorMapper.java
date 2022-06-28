package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderNumGenerator;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BasicPatternNumGeneratorMapper {

    Integer insertBasicPattern(PriorityOrderNumGenerator priorityOrderNumGenerator);
}
