package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityAllRestrictRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityAllRestrictRelationMapper {
   // void deleteWKTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void insertWKTableRelation(@Param("allRelationList") List<WorkPriorityAllRestrictRelation> allRelationList);

    void deleteWKTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
}
