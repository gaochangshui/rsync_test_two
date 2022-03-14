package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ShelfPtsJoinPatternDto;
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
    //sku数
    Integer getSkuNum(@Param("patternCd")Integer patternCd);
    //获取棚名称
    String getPengName(@Param("patternCd")Integer patternCd);
    //获取棚pattern名称
    String getPatternName(@Param("patternCd")Integer patternCd);

    //header/列名
    PtsDetailDataVo getPtsDetailData(@Param("patternCd")Integer patternCd);
    //获取tai信息
    List<PtsTaiVo> getTaiData(@Param("patternCd")Integer patternCd);
    //获取tana信息
    List<PtsTanaVo> getTanaData(@Param("patternCd")Integer patternCd);
    //获取janData信息
    List<PtsJanDataVo> getJanData(@Param("patternCd")Integer patternCd);

    int setDisplay(@Param("list")List< WorkPriorityOrderSort> workPriorityOrderSort,@Param("authorCd")String authorCd);

    List<WorkPriorityOrderSort> getDisplay(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

    int deleteDisplay(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

}
