package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.BasicPatternRestrictResult;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
* @author 10218504
* @createDate 2022-06-23 10:46:59
* @Entity generator.domain.BasicPatternRestrictResult
*/
@Mapper
public interface BasicPatternRestrictResultMapper {

    int deleteByPrimaryKey(Long id);

    int deleteByPriorityCd(Integer priorityOrderCd, String companyCd);

    int insert(BasicPatternRestrictResult record);

    int insertBatch(List<BasicPatternRestrictResult> lists);

    int insertSelective(BasicPatternRestrictResult record);

    List<Map<String, Object>> selectByPrimaryKey(Integer priorityOrderCd);

    List<Map<String, Object>> selectGroup(Integer priorityOrderCd,List<Map<String,Object>>list);

    int updateByPrimaryKeySelective(BasicPatternRestrictResult record);

    int updateByPrimaryKey(BasicPatternRestrictResult record);

    List<Map<String, Object>> selectJanZokusei(Integer priorityOrderCd, Integer ptsCd, List<ZokuseiMst> attrList, List<Integer> allCdList, String proInfoTable
            , String proMstInfo);

    void deleteFinal(String companyCd, String authorCd, Integer priorityOrderCd);

    void setFinalForWork(String companyCd, String authorCd, Integer priorityOrderCd);

    List<Map<String, Object>> selectNewJanZokusei(Integer priorityOrderCd, Integer ptsCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable,List<Map<String,Object>>attrName,List<Map<String, Object>> janSizeCol
            ,String tableName,Integer productPowerCd);

    List<Map<String, Object>> selectAllPatternResultData(Integer ptsCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable,List<Map<String,Object>>attrName, String proMstInfo);

    void setWorkForFinal(String companyCd, Integer priorityOrderCd,Integer newPriorityOrderCd);

    List<Map<String, Object>> getPtsJanInfo(Integer priorityOrderCd, Integer ptsCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable,List<Map<String,Object>>attrName,String tableName,
                                            List<Map<String,Object>> janSizeInfo);


    Map<String,Object> getGroupOld(Integer restrictCd, String companyCd, Integer priorityOrderCd,List<Map<String,Object>> attrList);


    List<Map<String, Object>> getPtsAllJan(Integer priorityOrderCd, Integer ptsCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable,List<Map<String,Object>>attrName,List<Map<String, Object>> janSizeCol,Integer productPowerCd);

    List<Map<String, Object>> selectOldJanZokusei(Integer priorityOrderCd, Integer ptsCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable,List<Map<String,Object>>attrName,List<Map<String, Object>> janSizeCol
            ,String tableName,Integer productPowerCd);

    List<Map<String,Object>> selectSortValue(Integer priorityOrderCd,String tableName);

    List<Map<String, Object>> selectOldJanZokuseiForFinal(Integer priorityOrderCd, Integer ptsCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable, List<Map<String, Object>> attrName, List<Map<String, Object>> janSizeCol, Integer productPowerCd);
}

