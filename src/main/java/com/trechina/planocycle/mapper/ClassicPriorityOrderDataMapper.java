package com.trechina.planocycle.mapper;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.dto.AttrHeaderSysDto;
import com.trechina.planocycle.entity.dto.DownloadDto;
import com.trechina.planocycle.entity.dto.GoodsRankDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(@Param("lists") JSONArray record,@Param("keyNameList") List<Map<String,String>> keyNameList,@Param("tablename") String tablename);

    int insertByPriorityOrderCd(@Param("lists") JSONArray record,@Param("keyNameList") List<Map<String,String>> keyNameList,@Param("priorityOrderCd") Integer priorityOrderCd);

    int dropTempData(String tablename);

    int updateTempData(@Param("lists") List<Map<String,String>> keyNameList,@Param("tablename") String tablename);

    List<Map<String,Object>> selectTempDataCol(@Param("lists") List<String> colNameList,
                                               @Param("priorityOrderCd") Integer priorityOrderCd,
                                               @Param("tablename") String tablename);
    List<Map<String,Object>> selectTempData(@Param("lists") List<String> colNameList,@Param("tablename") String tablename,@Param("mode")String mode);

    List<Map<String,Object>> selectTempDataAndMst(@Param("orderCol") List<String> colNameList,
                                                  @Param("colName") List<String> colName,
                                                  @Param("priorityOrderCd") Integer priorityOrderCd,
                                                  @Param("tablename") String tablename);

    int updatePriorityOrderDataForJanNew(String companyCd, Integer priorityOrderCd,String tablename);

    int updatePriorityOrderDataForCard(String companyCd, Integer priorityOrderCd,String tablename);

    int updatePriorityOrderDataForProp(String companyCd, Integer priorityOrderCd,String tablename);

    List<String> selectTempColName(String tablename);

    List<String> selectTempColNameBySchema(String tablename, String schema);

    String selectPriorityAttrCd(String company, Integer priorityNO);

    List<Map<String, Object>> selectPriorityAttrName(String tableNameAttr,String isCompanyCd,String prodIsCore);
    List<Map<String, Object>> selectSpecialName(String tableNameAttr,String isCompanyCd,String prodIsCore);

    String selectPriorityAttrValue(String company, Integer priorityNO);

    String selectJanNameFromJanNew(String janNew, String company, Integer priorityOrderCd);

    int deleteExistJanNew(List<String> janNews, String tableName);

    int deleteJanNew(String companyCd, Integer priorityOrderCd, String tablename);

    List<DownloadDto> downloadForCsv(@Param("tai") String tai,@Param("tana") String tana,@Param("priorityOrderCd")Integer priorityOrderCd
            ,@Param("colName")String colName);

    List<DownloadDto> downloadSavedForCsv(@Param("tai") String tai,@Param("tana") String tana,
                                          @Param("companyCd") String company, @Param("priorityOrderCd")Integer priorityOrderCd);

    void truncateTable(String tablename);

    List<GoodsRankDto> getGoodsRank(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int updateGoodsRank(@Param("list")List<GoodsRankDto> list,@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    List<PriorityOrderMstAttrSort> getAttrSort (String company, Integer priorityNO);

    int setRank(@Param("list")List<Map<String,Object>> lists ,@Param("tablename")String tablename);

    void updateCutJanForProp(String tablename);

    void updateCutJanByJanList(Integer priorityOrderCd, List<DownloadDto> existJanCut);

    List<String> selectExistJan(@Param("tableName")String tableName, List<DownloadDto> newJanList);

    List<Map<String, Object>> getTempDataAndMst(@Param("colSortNameList") List<String> colSortNameList,
                                                @Param("colNameList") List<String> colNameList,
                                                @Param("companyCd") String companyCd,
                                                @Param("priorityOrderCd") Integer priorityOrderCd);

    List<String> existJanOld(List<String> jan,Integer priorityOrderCd);

    List<String> existJanNew(List<String> jan,Integer priorityOrderCd);

    List<Map<String,Object>> getTmpTable(@Param("colNameList")List<String>colNameList,@Param("priorityOrderCd") Integer priorityOrderCd,@Param("companyCd")String companyCd);

    void updateUPdRank(@Param("janCutList") List<String> janCutList,@Param("companyCd") String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    void insertJanNew(@Param("lists") JSONArray jsonArray);

    void insertTmpTable( @Param("lists") List<Map<String, Object>> dataList);

    List<Map<String, Object>> selectTempDataByRankUpd(@Param("lists") List<String> attrSort, Integer priorityOrderCd,
                                                      String company, List<String> allAttrSortList);

    void updateRevivification(@Param("companyCd") String companyCd,@Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String, Object>> selectPriorityStratumName(String tableName,String isCompanyCd,String prodIsCore);

    List<Map<String, Object>> selectDynamicAttr(String[] jans, List<AttrHeaderSysDto> attrTableList, String coreTableName, String janCdCol, String janNameCol);

    String getColName(String tableNameAttr, String tableNameKaisou,String colName,String company);

    int deleteWorkData(String companyCd,Integer priorityOrderCd);

    int insertWorkData(String companyCd, Integer priorityOrderCd, List<Map<String, Object>> datas,String authorCd);

    int insertWorkDataForFinal(String companyCd, Integer priorityOrderCd,Integer newPriorityOrderCd);

    List<Map<String,Object>> getWorkData(String companyCd,Integer priorityOrderCd,List<String> attrList,List<String> attrSortList);

    List<Map<String, Object>> getJanBranchNumList(String aud, Integer priorityOrderCd, List<String> colNameList);

    List<Map<String, Object>> getData(Integer priorityOrderCd,List<String> colNameList);

    Map<String,Object> getJanBranchNum(Integer priorityOrderCd, String janOld,String janNew);

    List<Map<String, Object>> getDataNotExistNewJan(Integer priorityOrderCd);

    List<Map<String, Object>> getPriorityOrderMustAttr(List<String> attrList, String branchNum, Integer priorityOrderCd);

    List<Map<String,Object>> getJanInfo(Integer priorityOrderCd,String janInfoTableName);

    List<Map<String,Object>> getJanName(List<String> janList,Integer priorityOrderCd,String tableName);

    List<LinkedHashMap<String,Object>> getAttrForName(String companyCd, Integer priorityOrderCd);

    void setSpecialName(List<Map<String, Object>> linkedHashMaps, List<Map<String, Object>> attrSpecialList);
}
