package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.vo.PriorityAllPatternListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityAllMstMapper {

    List<TableNameDto> getTableNameByCompanyCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    /**
     * 基本パターンCDにより棚パターンCDを取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer getPatternCdBYPriorityCd(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    PriorityOrderMstDto getPriorityOrderMst(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    /**
     *
     * @param companyCd
     * @param priorityAllCd
     * @param priorityOrderCd
     * @param patternCd
     * @return
     */
    List<PriorityAllPatternListVO> getAllPatternData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("patternCd") Integer patternCd);

     String getPtsCd(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);

    void deleteWKTableMst(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTableShelfs(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTableRestrict(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTableResult(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTablePtsRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void delWKTablePtsTai(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void delWKTablePtsTana(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void delWKTablePtsJans(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void delWKTablePtsData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void delWKTablePtsVersion(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTablePtsTai(@Param("companyCd") String companyCd, @Param("ptsCd") int[] ptsCd, @Param("authorCd") String authorCd);
    void deleteWKTablePtsTana(@Param("companyCd") String companyCd, @Param("ptsCd") int[] ptsCd, @Param("authorCd") String authorCd);
    void deleteWKTablePtsJans(@Param("companyCd") String companyCd, @Param("ptsCd") int[] ptsCd, @Param("authorCd") String authorCd);
    void deleteWKTablePtsData(@Param("companyCd") String companyCd, @Param("ptsCd") int[] ptsCd, @Param("authorCd") String authorCd);
    void deleteWKTablePtsVersion(@Param("companyCd") String companyCd, @Param("ptsCd") int[] ptsCd, @Param("authorCd") String authorCd);

    void copyWKTableMst(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("newPriorityAllCd") Integer newPriorityAllCd);
    void copyWKTableShelfs(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("newPriorityAllCd") Integer newPriorityAllCd);
    void copyWKTableRestrict(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("newPriorityAllCd") Integer newPriorityAllCd);
    void copyWKTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("newPriorityAllCd") Integer newPriorityAllCd);
    void copyWKTableResult(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("newPriorityAllCd") Integer newPriorityAllCd);
    void copyWKTablePtsTai(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd
            , @Param("newPriorityAllCd") Integer newPriorityAllCd,@Param("id")Integer id,@Param("newId")Integer newId);
    void copyWKTablePtsTana(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd
            , @Param("newPriorityAllCd") Integer newPriorityAllCd,@Param("id")Integer id,@Param("newId")Integer newId);
    void copyWKTablePtsJans(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd
            , @Param("newPriorityAllCd") Integer newPriorityAllCd,@Param("id")Integer id,@Param("newId")Integer newId);
    void copyWKTablePtsData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd
            , @Param("newPriorityAllCd") Integer newPriorityAllCd,@Param("id")Integer id,@Param("newId")Integer newId);
    void copyWKTablePtsVersion(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd
            , @Param("newPriorityAllCd") Integer newPriorityAllCd,@Param("id")Integer id,@Param("newId")Integer newId);

    void insertWKTableMst(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);
    void insertWKTableShelfs(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd, @Param("patterns") List<Integer> patterns);


    void deleteFinalTableMst(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteFinalTableShelfs(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteFinalTablePtsTai(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteFinalTablePtsTana(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteFinalTablePtsJans(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteFinalTablePtsData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteFinalTablePtsVersion(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);

    //テンポラリ・テーブルの変更
    void setFinalTableMst(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("priorityAllName")String priorityAllName);
    void setFinalTableShelfs(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);
    void setFinalTableRestrict(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);
    void setFinalTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);
    void setFinalTableResult(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);
    void setFinalTablePtsTai(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);
    void setFinalTablePtsTana(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);
    void setFinalTablePtsJans(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);
    void setFinalTablePtsData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);
    void setFinalTablePtsVersion(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);


    Integer getPriorityOrderCd(@Param("priorityAllCd")Integer priorityAllCd,@Param("companyCd") String companyCd);


    void setNewFinalTableMst(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd,@Param("priorityAllName")String priorityAllName);
    void setNewFinalTableShelfs(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void setNewFinalTableRestrict(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void setNewFinalTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void setNewFinalTableResult(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void setNewFinalTablePtsTai(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void setNewFinalTablePtsTana(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void setNewFinalTablePtsJans(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void setNewFinalTablePtsData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void setNewFinalTablePtsVersion(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);


    //インタフェースの削除
    int deleteMst(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    int deleteShelfs(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    int deleteRestrict(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    int deleteResult(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    int deletePtsTai(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    int deletePtsTana(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    int deletePtsJans(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    int deletePtsData(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    int deletePtsRelation(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    int deletePtsVersion(@Param("companyCd")String companyCd,@Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);

    //チェック名
    Integer selectPriorityAllName(@Param("priorityAllName")String priorityAllName,@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

    int deleteFinalTableRestrictResult(String companyCd, Integer priorityAllCd, String authorCd);

    int deleteFinalTableRestrictRelation(String companyCd, Integer priorityAllCd, String authorCd);

    int deleteFinalTableRestrictResultData(String companyCd, Integer priorityAllCd, String authorCd);

    void setFinalTableRestrictResult(String companyCd, Integer priorityAllCd);

    void setFinalTableRestrictRelation(String companyCd, Integer priorityAllCd);

    void setFinalTableRestrictResultData(String companyCd, Integer priorityAllCd);

    Integer getWorkPriorityOrderCd(Integer priorityAllCd, String companyCd);
}
