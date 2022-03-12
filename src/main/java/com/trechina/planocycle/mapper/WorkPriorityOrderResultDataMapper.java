package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ProductPowerDataDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderResultData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderResultDataMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("janCd") String janCd);

    int insert(WorkPriorityOrderResultData record);

    int insertSelective(WorkPriorityOrderResultData record);

    WorkPriorityOrderResultData selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("janCd") String janCd);

    int updateByPrimaryKeySelective(WorkPriorityOrderResultData record);

    int updateByPrimaryKey(WorkPriorityOrderResultData record);

    int setResultDataList(@Param("list")List<ProductPowerDataDto> productPowerDataDtoList,@Param("restrictCd")Long restrictCd,@Param("companyCd")String companyCd,
                          @Param("authorCd")String authorCd);
    int delResultData(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    String getResultDataList(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);
}