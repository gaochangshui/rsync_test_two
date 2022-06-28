package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderNumGenerator;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PriorityOrderNumGeneratorMapper {

    Integer insert(PriorityOrderNumGenerator record);

    Integer insertPriority(PriorityOrderNumGenerator record);

    PriorityOrderNumGenerator selectByPrimaryKey(Integer id);


}
