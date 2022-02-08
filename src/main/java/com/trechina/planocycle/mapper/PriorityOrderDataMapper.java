package com.trechina.planocycle.mapper;

import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(@Param("lists") JSONArray record,@Param("keyNameList") List<Map<String,String>> keyNameList,@Param("tablename") String tablename);

    int dropTempData(String tablename);

    int updateTempData(@Param("lists") List<Map<String,String>> keyNameList,@Param("tablename") String tablename);

    List<Map<String,Object>> selectTempDataCol(@Param("lists") List<String> colNameList,
                                               @Param("priorityOrderCd") Integer priorityOrderCd,
                                               @Param("tablename") String tablename);
    List<Map<String,Object>> selectTempData(@Param("lists") List<String> colNameList,@Param("tablename") String tablename);

    List<Map<String,Object>> selectTempDataAndMst(@Param("orderCol") List<String> colNameList,
                                                  @Param("colName") List<String> colName,
                                                  @Param("priorityOrderCd") Integer priorityOrderCd,
                                                  @Param("tablename") String tablename);

    int updatePriorityOrderDataForJanNew(String companyCd, Integer priorityOrderCd,String tablename);

    int updatePriorityOrderDataForCard(String companyCd, Integer priorityOrderCd,String tablename);

    int updatePriorityOrderDataForProp(String companyCd, Integer priorityOrderCd,String tablename);

    List<String> selectTempColName(String tablename);

    String selectPriorityAttrCd(String company, Integer priorityNO);

    List<Map<String, Object>> selectPriorityAttrName(Integer productPowerCd);

    String selectPriorityAttrValue(String company, Integer priorityNO);

    String selectJanName(String jan_new);
}
