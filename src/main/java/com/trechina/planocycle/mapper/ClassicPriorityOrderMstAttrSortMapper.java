package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderMstAttrSortDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassicPriorityOrderMstAttrSortMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderMstAttrSort> record);

    int insertSelective(PriorityOrderMstAttrSort record);

    List<PriorityOrderMstAttrSort> selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int deleteAttrWk(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
    int insertAttrWk(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("attrList")List<String> attrList);

    List<PriorityOrderMstAttrSort> selectWKByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderMstAttrSortDto> selectWKRankSort(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderMstAttrSortDto> selectWKAttr(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
}
