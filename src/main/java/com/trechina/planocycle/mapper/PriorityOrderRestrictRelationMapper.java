package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PriorityOrderRestrictRelationMapper {

    int deleteByAuthorCd(@Param("companyCd") String companyCd,@Param("authorCd") String authorCd,
                         @Param("priorityOrderCd") Integer priorityOrderCd);


    int logicDeleteByPriorityOrderCd(@Param("companyCd") String companyCd,@Param("authorCd") String authorCd,
                                      @Param("priorityOrderCd") Integer priorityOrderCd);

    int insertBySelect(@Param("companyCd") String companyCd,@Param("authorCd") String authorCd,
                        @Param("priorityOrderCd") Integer priorityOrderCd);
}