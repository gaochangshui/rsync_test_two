package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.vo.PriorityAllPatternListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityAllMstMapper {

    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);

    List<TableNameDto> getTableNameByCompanyCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    /**
     * 基本パターンCDにより棚パターンCDを取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer getPatternCdBYPriorityCd(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    /**
     *
     * @param companyCd
     * @param priorityAllCd
     * @param priorityOrderCd
     * @param patternCd
     * @return
     */
    List<PriorityAllPatternListVO> getAllPatternData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("patternCd") Integer patternCd);

    void deleteWKTableMst(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTableShelfs(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTableRestrict(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTableResult(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTablePtsTai(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTablePtsTana(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void deleteWKTablePtsJans(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);

    void copyWKTableMst(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void copyWKTableShelfs(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void copyWKTableRestrict(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void copyWKTableRelation(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void copyWKTableResult(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void copyWKTablePtsTai(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void copyWKTablePtsTana(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);
    void copyWKTablePtsJans(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd);

    void insertWKTableMst(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);
    void insertWKTableShelfs(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd, @Param("authorCd") String authorCd, @Param("patterns") List<Integer> patterns);




}
