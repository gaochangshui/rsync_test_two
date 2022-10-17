package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderCompareJanData;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderCompareJanDataMapper {

    List<PriorityOrderCompareJanData> getPatternNewCompare(String companyCd,Integer priorityOrderCd);
    List<PriorityOrderCompareJanData> getAllNewCompare(String companyCd,Integer priorityOrderCd);

    Integer getAllSaleForecast(String companyCd,Integer priorityOrderCd);
    List<PriorityOrderCompareJanData> getAllOldCompare(String companyCd,Integer priorityOrderCd);

    List<Integer> getPatternList(String companyCd, Integer priorityOrderCd);

    List<Map<String,Object>> getChangeJanForAll(String companyCd, Integer priorityOrderCd);
    List<Map<String,Object>> getChangeJan(String companyCd, Integer priorityOrderCd);

    List<Map<String,Object>> getNewPtsAttrCompare(List<Map<String,Object>> attrResultList,Map<String,Object> listTableName, Integer priorityOrderCd);

    List<Map<String, Object>> getOldPtsAttrCompare(List<Map<String,Object>> attrResultList,Map<String,Object> listTableName, Integer priorityOrderCd);

    List<Map<String,Object>> getAttrValue(String companyCd, Integer priorityOrderCd);

    List<LinkedHashMap<String, Object>> getJanOldData(Integer shelfPatternCd, List<Map<String, Object>> attrList, Map<String, Object> listTableName,
                                                   List<Map<String, Object>> janSizeCol,String tableName);

    List<LinkedHashMap<String, Object>> getJanNewData(Integer shelfPatternCd, List<Map<String, Object>> attrList, Map<String, Object> listTableName
            , List<Map<String, Object>> janSizeCol, String tableName,Integer priorityOrderCd);

    int deleteCompareJandata(Integer priorityOrderCd);
    void insertCompareDeleteJandata(List<Map<String, Object>> deleteJanList, String companyCd, Integer priorityOrderCd, Integer shelfPatternCd,
                                    String branchCd, Integer compareFlag);
    void insertCompareNewJandata(List<Map<String, Object>> newJanList, String companyCd, Integer priorityOrderCd, Integer shelfPatternCd,
                                 String branchCd, Integer compareFlag);

    List<Map<String, Object>> selectAllCompareJandata(Integer priorityOrderCd);

    List<Map<String,Object>> getPatternBranchList(String companyCd, Integer priorityOrderCd,String tableName,List<String> groupCompany);

    List<String> getAllBranchList(String companyCd, Integer priorityOrderCd,String tableName,List<String> groupCompany);

    List<PriorityOrderCompareJanData> getPatternOldCompare(String companyCd, Integer priorityOrderCd);
}
