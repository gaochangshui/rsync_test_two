package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderResultData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkPriorityOrderResultDataMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("janCd") String janCd);

    int insert(WorkPriorityOrderResultData record);

    int insertSelective(WorkPriorityOrderResultData record);

    WorkPriorityOrderResultData selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("janCd") String janCd);

    int updateByPrimaryKeySelective(WorkPriorityOrderResultData record);

    int updateByPrimaryKey(WorkPriorityOrderResultData record);
}