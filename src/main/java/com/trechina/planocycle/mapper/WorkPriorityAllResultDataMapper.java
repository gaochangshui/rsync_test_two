package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityAllResultDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityAllResultDataMapper {
    int insertWKTableResultData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd,
                                @Param("priorityOrderCd") Integer priorityOrderCd,
                                @Param("authorCd") String authorCd, @Param("patternCd") Integer patternCd);

    int deleteWKTableResultData(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd,
                                @Param("authorCd") String authorCd);

    List<PriorityOrderResultDataDto> getResultJans(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd,
                                                 @Param("authorCd") String authorCd, @Param("patternCd") Integer patternCd);

    void updateTaiTanaBatch(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd,@Param("patternCd") Integer patternCd,
                            @Param("authorCd") String authorCd,@Param("list") List<PriorityOrderResultDataDto> resultDataDtos);

    String getJans(@Param("patternCd") Integer patternCd,@Param("companyCd") String companyCd,@Param("priorityAllCd") Integer priorityAllCd);

    int update(@Param("list") List<PriorityAllResultDataDto> list, @Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("priorityAllCd")Integer priorityAllCd,
                   @Param("patternCd")Integer patternCd);
    Double getAvgSalesCunt( @Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("priorityAllCd")Integer priorityAllCd,
                    @Param("patternCd")Integer patternCd);
    List<PriorityAllResultDataDto>  getResultDatas( @Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("priorityAllCd")Integer priorityAllCd,
                            @Param("patternCd")Integer patternCd);

    int updateFace(@Param("list") List<PriorityAllResultDataDto> list);

}
