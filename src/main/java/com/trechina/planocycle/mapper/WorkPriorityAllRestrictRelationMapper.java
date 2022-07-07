package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityAllRestrictRelation;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkPriorityAllRestrictRelationMapper {
   // void deleteWKTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void insertWKTableRelation(@Param("allRelationList") List<WorkPriorityAllRestrictRelation> allRelationList);

    void deleteWKTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);

    List<WorkPriorityOrderRestrictRelation> selectByAuthorCd(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd,
                                                             @Param("authorCd") String authorCd,@Param("shelfPatternCd") Integer shelfPatternCd);

    void deleteFinalTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);

    int setBasicPatternRelation(String companyCd, Integer priorityOrderCd, Integer shelfPatternCd,Integer priorityAllCd,String authorCd);

 int  deleteBasicPatternRelation(String companyCd, Integer priorityAllCd, String authorCd,Integer patternCd);
    List<Map<String, Object>> selectRelation(Integer priorityAllCd, Integer patternCd);
}
