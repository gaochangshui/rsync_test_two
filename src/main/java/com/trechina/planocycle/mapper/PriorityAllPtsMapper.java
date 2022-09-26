package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityAllPtsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.PtsDetailDataVo;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import com.trechina.planocycle.entity.vo.PtsTanaVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityAllPtsMapper {
    ShelfPtsData selectWorkPtsCdByAuthorCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,
                                           @Param("priorityAllCd") Integer priorityAllCd, @Param("patternCd") Integer patternCd);

    void deletePtsData(Integer oldPtsCd);

    void deletePtsTaimst(Integer oldPtsCd);

    void deletePtsTanamst(Integer oldPtsCd);

    void deletePtsVersion(Integer oldPtsCd);

    void deletePtsDataJandata(Integer oldPtsCd);

    void insertPtsData(PriorityAllPtsDataDto priorityAllPtsDataDto);

    void insertPtsTaimst(Integer ptsCd, Integer id, String authorCd, Integer priorityAllCd, Integer patternCd);

    void insertPtsTanamst(Integer ptsCd, Integer id, String authorCd, Integer priorityAllCd, Integer patternCd);

    void insertPtsVersion(Integer ptsCd, Integer id, String authorCd, Integer priorityAllCd, Integer patternCd);

    void insertPtsDataJandata(@Param("list") List<PriorityOrderResultDataDto> positionResultData,
                              Integer id, String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd);

    List<PtsTaiVo> getTaiData(Integer id);

    List<PtsTanaVo> getTanaData(Integer id);

    List<LinkedHashMap> getJanData(@Param("id") Integer id, @Param("attrList")List<Map<String,Object>> attrList
            , @Param("tableName")String tableName, @Param("janSizeCol")List<Map<String,Object>>janSizeCol, String proTableName);

    PtsDetailDataVo getPtsDetailData(String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd);

    ShelfPtsDataVersion selectAllVersionByPtsCd(String companyCd, Integer ptsCd);

    List<ShelfPtsDataTaimst> selectAllTaimstByPtsCd(String companyCd, Integer ptsCd);

    List<ShelfPtsDataTanamst> selectAllTanamstByPtsCd(String companyCd, Integer ptsCd);

    List<ShelfPtsDataJandata> selectAllJandataByPtsCd(String companyCd, Integer ptsCd);

    ShelfPtsData selectPtsCdByAuthorCd(String companyCd, String authorCd, Integer priorityAllCd, Long shelfPatternCd);

    List<ShelfPtsData> selectByPriorityAllCd(String companyCd, String authorCd, Integer priorityAllCd);

    List<Map<String, Object>> selectTanaMstByPatternCd(Integer priorityAllCd, Integer patternCd);

    Integer getPtsCd();

    Integer getNewFaceNum(Integer ptsCd);
    Integer getNewSkuNum(Integer ptsCd);
    Integer getNewTaiNum(Integer ptsCd);

    Integer getNewTanaNum(Integer ptsCd);

    Integer getNewId(String companyCd,Integer priorityAllCd,Integer patternCd);

    List<Map<String, Object>> selectNewJanZokusei(Integer priorityOrderCd, Integer ptsCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable,List<Map<String,Object>>attrName,List<Map<String, Object>> janSizeCol);
}

