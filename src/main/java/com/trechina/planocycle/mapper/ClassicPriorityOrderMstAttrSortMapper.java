package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ClassicPriorityOrderMstAttrSortDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstAttrSortDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    int insertAttrForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
    int insertAttrSortForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<String> getAttrSortList(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderMstAttrSort> selectWKByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderMstAttrSortDto> selectWKRankSort(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderMstAttrSortDto> selectWKAttr(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<String>getAttrList(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String,Object>>getAllAttrList(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);



    String getAttrCol(String companyCd, Integer priorityOrderCd, String value);

    List<String> attrValueList(String companyCd, Integer priorityOrderCd);

    List<ClassicPriorityOrderMstAttrSortDto> selectAttrName(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

}
