package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityAllPtsDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.ShelfPtsData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityAllPtsMapper {
    ShelfPtsData selectWorkPtsCdByAuthorCd(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd,
                                           @Param("priorityAllCd")Integer priorityAllCd, @Param("patternCd") Integer patternCd);

    void deletePtsData(Integer oldPtsCd);

    void deletePtsTaimst(Integer oldPtsCd);

    void deletePtsTanamst(Integer oldPtsCd);

    void deletePtsVersion(Integer oldPtsCd);

    void deletePtsDataJandata(Integer oldPtsCd);

    void insertPtsData(PriorityAllPtsDataDto priorityAllPtsDataDto);

    void insertPtsTaimst(Integer ptsCd, Integer id, String authorCd, Integer priorityAllCd, Integer patternCd);

    void insertPtsTanamst(Integer ptsCd, Integer id, String authorCd, Integer priorityAllCd, Integer patternCd);

    void insertPtsVersion(Integer ptsCd, Integer id, String authorCd, Integer priorityAllCd, Integer patternCd);

    void insertPtsDataJandata(@Param("list") List<WorkPriorityOrderResultDataDto> positionResultData,
                              Integer id, String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd);
}
