package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderZokuseiOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkPriorityOrderZokuseiOrderMapper {
    int insert(WorkPriorityOrderZokuseiOrder record);

    int insertSelective(WorkPriorityOrderZokuseiOrder record);
}