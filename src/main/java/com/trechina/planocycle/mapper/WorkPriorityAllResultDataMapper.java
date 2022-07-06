package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityAllResultDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderResultData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkPriorityAllResultDataMapper {
    int insertWKTableResultData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd,
                                @Param("authorCd") String authorCd, @Param("patternCd") Integer patternCd,@Param("janList")List<Map<String,Object>> janList);

    int deleteWKTableResultData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd,
                                @Param("authorCd") String authorCd);

    List<PriorityOrderResultDataDto> getResultJans(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd,
                                                 @Param("authorCd") String authorCd, @Param("patternCd") Integer patternCd,  @Param("priorityOrderCd")Integer priorityOrderCd);

    void updateTaiTanaBatch(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd,@Param("patternCd") Integer patternCd,
                            @Param("authorCd") String authorCd,@Param("list") List<PriorityOrderResultDataDto> resultDataDtos);

    String getJans(@Param("patternCd") Integer patternCd,@Param("companyCd") String companyCd,@Param("priorityAllCd") Integer priorityAllCd,@Param("authorCd")String authorCd);

    int update(@Param("list") List<PriorityAllResultDataDto> list, @Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("priorityAllCd")Integer priorityAllCd,
                   @Param("patternCd")Integer patternCd);
    Double getAvgSalesCunt( @Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("priorityAllCd")Integer priorityAllCd,
                    @Param("patternCd")Integer patternCd);
    List<PriorityAllResultDataDto>  getResultDatas( @Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("priorityAllCd")Integer priorityAllCd,
                            @Param("patternCd")Integer patternCd);

    int updateFace(@Param("list") List<PriorityAllResultDataDto> list);

    List<WorkPriorityOrderResultData> getReorder(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("productPowerCd")Integer productPowerCd,
                                                 @Param("priorityAllCd")Integer priorityAllCd, @Param("commonTableName")GetCommonPartsDataDto getCommonPartsDataDto
            , @Param("sortName1")String sortName1,@Param("patternCd")Integer patternCd, @Param("sortName2")String sortName2);

    void setSortRank(List<WorkPriorityOrderResultData> reorder, String companyCd, String authorCd, Integer priorityOrderCd);

}
