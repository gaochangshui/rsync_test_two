package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

@Mapper
public interface ClassicPriorityOrderMstAttrSortMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderMstAttrSort> record);

    int insertSelective(PriorityOrderMstAttrSort record);

    List<PriorityOrderMstAttrSort> selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int deleteAttrWk(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
    int insertAttrWk(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("attrList")List<LinkedHashMap<String,Object>> attrList);

    int deleteAttrSortWK(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
    int insertAttrSortWk(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("attrList")List<String> attrList);

    List<String>getAttrSortList(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
    List<String>getAttrList(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);


    String getAttrCol(String companyCd, Integer priorityOrderCd, String value);
}
