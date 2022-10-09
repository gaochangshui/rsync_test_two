package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderCompareJanData;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderCompareJanDataMapper {

    List<PriorityOrderCompareJanData> getPatternCompare(String companyCd,Integer priorityOrderCd,String tableName,List<String> groupCompany);
    List<PriorityOrderCompareJanData> getAllCompare(String companyCd,Integer priorityOrderCd,String tableName,List<String> groupCompany);

    List<Integer> getPatternList(String companyCd, Integer priorityOrderCd);

    List<Map<String,Object>> getChangeJanForAll(String companyCd, Integer priorityOrderCd);
    List<Map<String,Object>> getChangeJan(String companyCd, Integer priorityOrderCd);

    List<Map<String,Object>> getNewPtsAttrCompare(List<String> attr, Integer priorityOrderCd);

    List<Map<String, Object>> getOldPtsAttrCompare(List<String> attr, Integer priorityOrderCd);

    List<Map<String,Object>> getAttrValue(String companyCd, Integer priorityOrderCd);

    List<LinkedHashMap<String, Object>> getJanOldData(Integer shelfPatternCd, List<Map<String, Object>> attrList, Map<String, Object> listTableName,
                                                   List<Map<String, Object>> janSizeCol,String tableName);

    List<LinkedHashMap<String, Object>> getJanNewData(Integer shelfPatternCd, List<Map<String, Object>> attrList, Map<String, Object> listTableName
            , List<Map<String, Object>> janSizeCol, String tableName);

    int deleteCompareJandata(Integer priorityOrderCd);
    void insertCompareDeleteJandata(List<Map<String, Object>> deleteJanList, String companyCd, Integer priorityOrderCd, Integer shelfPatternCd,
                                    String branchCd);
    void insertCompareNewJandata(List<Map<String, Object>> newJanList, String companyCd, Integer priorityOrderCd, Integer shelfPatternCd,
                                 String branchCd);
}
