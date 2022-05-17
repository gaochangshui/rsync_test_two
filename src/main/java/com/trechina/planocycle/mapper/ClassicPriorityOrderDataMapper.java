package com.trechina.planocycle.mapper;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.dto.DownloadDto;
import com.trechina.planocycle.entity.dto.GoodsRankDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(@Param("lists") JSONArray record,@Param("keyNameList") List<Map<String,String>> keyNameList,@Param("tablename") String tablename);


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

    String selectPriorityAttrCd(String company, Integer priorityNO);

    List<Map<String, Object>> selectPriorityAttrName(Integer productPowerCd);

    String selectPriorityAttrValue(String company, Integer priorityNO);

    String selectJanName(String jan_new);

    String selectJanNameFromJanNew(String jan_new, String company, Integer priorityOrderCd);

    int deleteExistJanNew(List<String> janNews, String tableName);

    int deleteJanNew(String companyCd, Integer priorityOrderCd, String tablename);

    List<DownloadDto> downloadForCsv(@Param("tai") String tai,@Param("tana") String tana,@Param("tablename") String tablename,@Param("priorityOrderCd")Integer priorityOrderCd);

    List<DownloadDto> downloadSavedForCsv(@Param("tai") String tai,@Param("tana") String tana,
                                          @Param("companyCd") String company, @Param("priorityOrderCd")Integer priorityOrderCd);

    void truncateTable(String tablename);

    List<GoodsRankDto> getGoodsRank(@Param("tablename")String tablename);

    int updateGoodsRank(@Param("tablename")String tablename,@Param("list")List<GoodsRankDto> list);

    List<PriorityOrderMstAttrSort> getAttrSort (String company, Integer priorityNO);

    int setRank(@Param("list")List<Map<String,Object>> lists ,@Param("tablename")String tablename);

    void updateCutJanForProp(String tablename);

    void updateCutJanByJanList(String tablename, List<DownloadDto> existJanCut);

    List<String> selectExistJan(@Param("tableName")String tableName, List<DownloadDto> newJanList);

    List<Map<String, Object>> getTempDataAndMst(@Param("orderCol") List<String> colNameList,
                                                @Param("colName") List<String> colName,
                                                @Param("priorityOrderCd") Integer priorityOrderCd,
                                                @Param("tablename") String tablename);

    List<String> existJanOld(List<String> jan, String tableName);

    List<String> existJanNew(List<String> jan, String tableName);

    List<Map<String,Object>> getTmpTable(@Param("tableName") String tableName,@Param("colName")List<String>colName,@Param("priorityOrderCd") Integer priorityOrderCd,@Param("companyCd")String companyCd);

    void updateUPdRank(@Param("janCutList") List<String> janCutList,@Param("tableName") String tableName);

    void insertJanNew(@Param("lists") JSONArray jsonArray,@Param("tableName")String tableName);

    void insertTmpTable(@Param("tableName") String tableName, @Param("lists") List<Map<String, Object>> dataList);

    List<Map<String, Object>> selectTempDataByRankUpd(@Param("lists") List<String> attrSort, String tableName);

    void updateRevivification(@Param("tableName")String tableName);
}