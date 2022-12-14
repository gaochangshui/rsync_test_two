package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.ShelfPtsData;
import com.trechina.planocycle.entity.po.ShelfPtsDataTaimst;
import com.trechina.planocycle.entity.po.ShelfPtsDataTanamst;
import com.trechina.planocycle.entity.po.WorkPriorityOrderSort;
import com.trechina.planocycle.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ShelfPtsDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShelfPtsData record);

    List<ShelfPtsData> selectByPrimaryKey(String companyCd);

    int updateByPrimaryKey(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    void updateByValidFlg(String companyCd);

    void updatePtsHistory(@Param("item") ShelfPtsJoinPatternDto item, @Param("authorCd") String authorCd);

    void insertPtsHistory(@Param("item") ShelfPtsJoinPatternDto item, @Param("authorCd") String authorCd);

    Integer selectExistsCount(ShelfPtsJoinPatternDto item);

    void updatePtsHistoryFlg(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    List<ShelfPtsDataHistoryVO> selectHistoryData(String companyCd);

    List<ShelfPtsNameVO> selectPtsName(String companyCd);

    List<ShelfPtsData> selectPtsInfoOfPattern(String companyCd);

    void updateByPrimaryKeyOfPattern(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    Integer checkPtsData(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    Integer checkPatternData(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    void updateAll(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    void updateShelfPtsOfAutoInner(@Param("id") Integer id, @Param("patternId") Integer patternId, @Param("authorCd") String authorCd);

    void updateSingle(@Param("patternId") Integer patternId, @Param("authorCd") String authorCd);
    void updateSingleList(@Param("list") List<Integer> patternIdList, @Param("authorCd") String authorCd);

    void updatePtsHistoryFlgSingle(@Param("patternId") Integer patternId, @Param("authorCd") String authorCd);

    Integer delShelfPtsInfo(@Param("id") Integer id, @Param("authorCd") String authorCd);

    Integer delShelfHistoryInfo(@Param("id") Integer id, @Param("authorCd") String authorCd);

    //pts??????
    Integer getTaiNum(@Param("patternCd")Integer patternCd);
    //???pts??????
    Integer getNewTaiNum(@Param("priorityOrderCd")Integer priorityOrderCd);
    //final???pts??????
    Integer getNewTaiNumFinal(@Param("priorityOrderCd")Integer priorityOrderCd);
    //pts?????????????????????
    Integer getTanaNum(@Param("patternCd")Integer patternCd);

    Integer getNewTanaNum(@Param("priorityOrderCd")Integer priorityOrderCd);

    Integer getNewTanaNumFinal(@Param("priorityOrderCd")Integer priorityOrderCd);
    //pts??????
    Map<String,Object> getNewTanaWidth(@Param("priorityOrderCd")Integer priorityOrderCd);
    //face???
    Integer getFaceNum(@Param("patternCd")Integer patternCd);
    //?????????face???
    Integer getNewFaceNum(@Param("priorityOrderCd")Integer priorityOrderCd);
    //final?????????face???
    Integer getNewFaceNumFinal(@Param("priorityOrderCd")Integer priorityOrderCd);
    //sku???
    Integer getSkuNum(@Param("patternCd")Integer patternCd);
    //?????????sku???
    Integer getNewSkuNum(@Param("priorityOrderCd")Integer priorityOrderCd);

    //final?????????sku???
    Integer getNewSkuNumFinal(@Param("priorityOrderCd")Integer priorityOrderCd);

    //?????????????????????
    String getPengName(@Param("patternCd")Integer patternCd);
    //???pattern????????????
    String getPatternName(@Param("patternCd")Integer ternCd);

    Integer getpatternCd(@Param("priorityOrderCd")Integer priorityOrderCd);

    //header/??????
    PtsDetailDataVo getPtsDetailData(@Param("patternCd")Integer patternCd);
    //?????????????????????/??????
    PtsDetailDataVo getPtsNewDetailData(@Param("priorityOrderCd")Integer priorityOrderCd);
    //ptsCd?????????
    Integer getPtsCd(@Param("patternCd")Integer patternCd);

    //??????tai??????
    List<PtsTaiVo> getTaiData(@Param("patternCd")Integer patternCd);
    //?????????tai??????
    List<PtsTaiVo> getNewTaiData(@Param("priorityOrderCd")Integer priorityOrderCd);

    //??????tana??????
    List<PtsTanaVo> getTanaData(@Param("patternCd")Integer patternCd);
    //?????????tana??????
    List<PtsTanaVo> getNewTanaData(@Param("priorityOrderCd")Integer priorityOrderCd);
    //??????janData??????
    List<LinkedHashMap<String,Object>> getJanData(@Param("patternCd")Integer patternCd, @Param("attrList")List<Map<String,Object>> attrList
            ,@Param("tableName")String tableName,@Param("janSizeCol")List<Map<String,Object>>janSizeCol, String proTableName);
    //?????????janData??????
    List<PtsJanDataVo> getNewJanData(@Param("priorityOrderCd")Integer priorityOrderCd);

    List<LinkedHashMap<String,Object>> getNewJanDataTypeMap(@Param("priorityOrderCd")Integer priorityOrderCd,
                                                            @Param("attrList")List<Map<String,Object>> attrList
                                                            ,@Param("tableName")String tableName,@Param("janSizeCol")List<Map<String,Object>>janSizeCol, String proTableName);

    int setDisplay(@Param("list")List< WorkPriorityOrderSort> workPriorityOrderSort,@Param("authorCd")String authorCd);

    List<WorkPriorityOrderSort> getDisplay(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);
    List<WorkPriorityOrderSortVo> getDisplays(@Param("companyCd")String companyCd
            ,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd
            ,@Param("company") String company,@Param("classCd")String classCd);

    int deleteDisplay(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int insertPtsData(PriorityOrderPtsDataDto ptsData);

    int insertPtsTaimst(@Param("ptsCd") Integer ptsCd, @Param("id") Integer id, @Param("authorCd") String authorCd);

    int insertPtsTanamst(@Param("ptsCd") Integer ptsCd, @Param("id") Integer id, @Param("authorCd") String authorCd);

    int insertPtsVersion(@Param("ptsCd") Integer ptsCd, @Param("id") Integer id, @Param("authorCd") String authorCd);

    int insertPtsDataJandata(@Param("list")List<PriorityOrderResultDataDto> positionResultData,
                             @Param("id") Integer id, @Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    int deletePtsTaimst(@Param("ptsCd")Integer ptsCd);
    //???????????????????????????????????????Data
    int deleteWorkPtsTaimst(@Param("ptsCd")Integer ptsCd);

    int deletePtsTanamst(@Param("ptsCd")Integer ptsCd);

    int deletePtsVersion(@Param("ptsCd")Integer ptsCd);

    int deletePtsData(@Param("ptsCd")Integer ptsCd);

    //???????????????????????????????????????Data
    int deleteWorkPtsData(@Param("ptsCd")Integer ptsCd);

    int deletePtsDataJandata(@Param("ptsCd")Integer ptsCd);
    int deletePtsJandataByPriorityOrderCd(Integer priorityOrderCd);

    ShelfPtsData selectPtsCdByAuthorCd(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,
                              @Param("priorityOrderCd")Integer priorityOrderCd, @Param("patternCd") Long patternCd);

    ShelfPtsData selectWorkPtsCdByAuthorCd(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,
                                       @Param("priorityOrderCd")Integer priorityOrderCd, @Param("patternCd") Long patternCd);
    int insertFinalPtsData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int insertFinalPtsTaiData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd);

    int insertFinalPtsTanaData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd);

    int insertFinalPtsVersionData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd);

    int insertFinalPtsDataJanData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd);


    Integer getId(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);
    Integer getIdCommon(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd,@Param("tableName")String tableName
                        ,@Param("ptsFlag")int ptsFlag,@Param("patternCd")Integer patternCd);
    Integer getNewId(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);


    //???????????????data
    int deleteFinalPtsData(@Param("ptsCd")Integer ptsCd);
    int deleteFinalPtsTaimst(@Param("ptsCd")Integer ptsCd);
    int deleteFinalPtsTanamst(@Param("ptsCd")Integer ptsCd);
    int deleteFinalPtsVersion(@Param("ptsCd")Integer ptsCd);
    int deleteFinalPtsDataJandata(@Param("ptsCd")Integer ptsCd);

    //???????????????????????????
    int insertWorkPtsData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd
    ,@Param("newPriorityOrderCd")Integer newPriorityOrderCd,@Param("newId")Integer newId);
    int insertWorkPtsTaiData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd
            ,@Param("newId")Integer newId);
    int insertWorkPtsTanaData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd
            ,@Param("newId")Integer newId);
    int insertWorkPtsVersionData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd
            ,@Param("newId")Integer newId);
    int insertWorkPtsJanData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd
            ,@Param("newId")Integer newId);

    ShelfPtsData selectPtsCdByPatternCd(@Param("companyCd") String companyCd, @Param("patternCd") Long shelfPatternCd);

    List<ShelfPtsData> getPtsCdByPatternCd(@Param("companyCd") String companyCd, @Param("patternCd") String shelfPatternCd);

    List<Map<String,Object>> getInitialExtraction(List<ShelfPtsData> shelfPtsData, String tableName, Integer productPowerCd
            , Map<String,Object> attrTableName, List<Map<String,Object>> listAttr,String colName,List<Integer> comparePtsList,
                                                  List<Integer> exceptJanPtsCd);

    List<LinkedHashMap<String, Object>> getColHeader(Map<String, Object> listTableName, List<Map<String, Object>> listAttr);

    void deleteTana(Integer taiCd, Integer id);

    void updTanaSize(List<PtsTanaVo> ptsTanaVoList, Integer id, String authorCd, String companyCd);

    void insertPtsData1(PriorityOrderPtsDataDto priorityOrderPtsDataDto);

    ShelfPtsHeaderDto selectShelfPts(Integer shelfPatternCd, String companyCd);

    List<Map<String, Object>> selectClassifyPtsData(List<String> rankAttr, Integer patternCd, Integer priorityOrderCd);

    List<ShelfPtsDataTaimst> selectShelfPtsTaiMst(Integer ptsCd);

    List<ShelfPtsDataTanamst> selectShelfPtsTanaMst(Integer ptsCd);

    List<LinkedHashMap> getJanDataAndId(@Param("patternCd")Integer patternCd, @Param("attrList")List<Map<String,Object>> attrList
            ,@Param("tableName")String tableName,@Param("janSizeCol")List<Map<String,Object>>janSizeCol);

    int selectJanCount(Integer priorityOrderCd, Integer taiCd, Integer tanaCd, Long restrictCd, Long id);

    void updatePtsAndPattern(List<PtsPatternRelationDto> ptsPatternRelationDtoList);

    List<Integer> getPtsCdForShelfName(String companyCd, Integer priorityPowerCd);

    List<Integer> getExceptJanPtsCd(String companyCd, Integer priorityPowerCd);
}
