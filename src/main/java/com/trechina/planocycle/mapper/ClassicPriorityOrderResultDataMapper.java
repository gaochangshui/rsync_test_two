package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderResultDataMapper {
    int setFinalForWork(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd") String authorCd);

    int deleteFinal(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);
    List<Map<String, Object>> selectFinalData(Integer priorityOrderCd, String companyCd,List<String> list);

    //select the branch num of the same attribute Jan
    Integer selectBranchNumByAttr(Integer priorityOrderCd, String companyCd, Map<String, Object> attrValMap);

    List<Map<String, Object>> selectFinalDataByAttr(Integer priorityOrderCd, String companyCd,List<String> attrList);

    List<Map<String, Object>> selectFinalDataByJan(Integer priorityOrderCd, String companyCd,List<String> attrList,List<String> janList);
}
