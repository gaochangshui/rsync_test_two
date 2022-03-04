package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ShelfPtsJoinPatternDto;
import com.trechina.planocycle.entity.po.ShelfPtsData;
import com.trechina.planocycle.entity.vo.ShelfPtsDataHistoryVO;
import com.trechina.planocycle.entity.vo.ShelfPtsNameVO;
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
}
