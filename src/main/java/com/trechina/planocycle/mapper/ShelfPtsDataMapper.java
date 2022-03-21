package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderPtsDataDto;
import com.trechina.planocycle.entity.dto.ShelfPtsJoinPatternDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.ShelfPtsData;
import com.trechina.planocycle.entity.po.WorkPriorityOrderSort;
import com.trechina.planocycle.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelfPtsDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShelfPtsData record);

    List<ShelfPtsData> selectByPrimaryKey(String companyCd, @Param("rangFlag") Integer rangFlag, @Param("lists") List<Integer> areaList);

    int updateByPrimaryKey(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    void updateByValidFlg(String companyCd);

    void updatePtsHistory(@Param("item") ShelfPtsJoinPatternDto item, @Param("authorCd") String authorCd);

    void insertPtsHistory(@Param("item") ShelfPtsJoinPatternDto item, @Param("authorCd") String authorCd);

    Integer selectExistsCount(ShelfPtsJoinPatternDto item);

    void updatePtsHistoryFlg(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    List<ShelfPtsDataHistoryVO> selectHistoryData(String companyCd);

    List<ShelfPtsNameVO> selectPtsName(String companyCd);

    List<ShelfPtsData> selectPtsInfoOfPattern(String companyCd, Integer rangFlag, @Param("lists") List<Integer> areaList);

    void updateByPrimaryKeyOfPattern(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    Integer checkPtsData(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    Integer checkPatternData(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    void updateAll(@Param("lists") List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    void updateShelfPtsOfAutoInner(@Param("id") Integer id, @Param("patternId") Integer patternId, @Param("authorCd") String authorCd);

    void updateSingle(@Param("patternId") Integer patternId, @Param("authorCd") String authorCd);

    void updatePtsHistoryFlgSingle(@Param("patternId") Integer patternId, @Param("authorCd") String authorCd);

    Integer delShelfPtsInfo(@Param("id") Integer id, @Param("authorCd") String authorCd);

    Integer delShelfHistoryInfo(@Param("id") Integer id, @Param("authorCd") String authorCd);

    //pts台数
    Integer getTaiNum(@Param("patternCd")Integer patternCd);
    //pts总段数
    Integer getTanaNum(@Param("patternCd")Integer patternCd);
    //face数
    Integer getFaceNum(@Param("patternCd")Integer patternCd);
    //新face数
    Integer getNewFaceNum(@Param("priorityOrderCd")Integer priorityOrderCd);
    //sku数
    Integer getSkuNum(@Param("patternCd")Integer patternCd);
    //新sku数
    Integer getNewSkuNum(@Param("priorityOrderCd")Integer priorityOrderCd);
    //获取棚名称
    String getPengName(@Param("patternCd")Integer patternCd);
    //获取棚pattern名称
    String getPatternName(@Param("patternCd")Integer ternCd);



    //header/列名
    PtsDetailDataVo getPtsDetailData(@Param("patternCd")Integer patternCd);
    //新header/列名
    PtsDetailDataVo getPtsNewDetailData(@Param("priorityOrderCd")Integer priorityOrderCd);
    //获取ptsCd
    Integer getPtsCd(@Param("patternCd")Integer patternCd);

    //获取tai信息
    List<PtsTaiVo> getTaiData(@Param("patternCd")Integer patternCd);
    //获取新tai信息
    List<PtsTaiVo> getNewTaiData(@Param("priorityOrderCd")Integer priorityOrderCd);

    //获取tana信息
    List<PtsTanaVo> getTanaData(@Param("patternCd")Integer patternCd);
    //获取新tana信息
    List<PtsTanaVo> getNewTanaData(@Param("priorityOrderCd")Integer priorityOrderCd);
    //获取janData信息
    List<PtsJanDataVo> getJanData(@Param("patternCd")Integer patternCd);
    //获取旧janData信息
    List<PtsJanDataVo> getNewJanData(@Param("priorityOrderCd")Integer priorityOrderCd);

    int setDisplay(@Param("list")List< WorkPriorityOrderSort> workPriorityOrderSort,@Param("authorCd")String authorCd);

    List<WorkPriorityOrderSort> getDisplay(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);
    List<WorkPriorityOrderSortVo> getDisplays(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int deleteDisplay(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

    int insertPtsData(PriorityOrderPtsDataDto ptsData);

    int insertPtsTaimst(@Param("ptsCd") Integer ptsCd, @Param("id") Integer id, @Param("authorCd") String authorCd);

    int insertPtsTanamst(@Param("ptsCd") Integer ptsCd, @Param("id") Integer id, @Param("authorCd") String authorCd);

    int insertPtsVersion(@Param("ptsCd") Integer ptsCd, @Param("id") Integer id, @Param("authorCd") String authorCd);

    int insertPtsDataJandata(@Param("list")List<WorkPriorityOrderResultDataDto> positionResultData,
                             @Param("id") Integer id, @Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    int deletePtsTaimst(@Param("ptsCd")Integer ptsCd);
    //删除临时表的Data
    int deleteWorkPtsTaimst(@Param("ptsCd")Integer ptsCd);

    int deletePtsTanamst(@Param("ptsCd")Integer ptsCd);

    int deletePtsVersion(@Param("ptsCd")Integer ptsCd);

    int deletePtsData(@Param("ptsCd")Integer ptsCd);

    //删除临时表的Data
    int deleteWorkPtsData(@Param("ptsCd")Integer ptsCd);

    int deletePtsDataJandata(@Param("ptsCd")Integer ptsCd);

    ShelfPtsData selectPtsCdByAuthorCd(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,
                              @Param("priorityOrderCd")Integer priorityOrderCd, @Param("patternCd") Long patternCd);


    int insertFinalPtsData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int insertFinalPtsTaiData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd);

    int insertFinalPtsTanaData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd);

    int insertFinalPtsVersionData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd);

    int insertFinalPtsDataJanData(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("ptsCd")Integer ptsCd);


    Integer getId(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);


    //删除最终表data
    int deleteFinalPtsData(@Param("ptsCd")Integer ptsCd);
    int deleteFinalPtsTaimst(@Param("ptsCd")Integer ptsCd);
    int deleteFinalPtsTanamst(@Param("ptsCd")Integer ptsCd);
    int deleteFinalPtsVersion(@Param("ptsCd")Integer ptsCd);
    int deleteFinalPtsDataJandata(@Param("ptsCd")Integer ptsCd);

    ShelfPtsData selectPtsCdByPatternCd(@Param("companyCd") String companyCd, @Param("patternCd") Long shelfPatternCd);
}
