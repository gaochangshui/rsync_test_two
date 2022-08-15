package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ProductPowerDataDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderRestrictResultMapper {
    int insert(WorkPriorityOrderRestrictResult record);

    int insertSelective(WorkPriorityOrderRestrictResult record);

    int insertAll(List<WorkPriorityOrderRestrictResult> list);

    int deleteByAuthorCd(String companyCd, String authorCd,Integer priorityOrderCd);
    List<WorkPriorityOrderRestrictResult> getResultList(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    List<ProductPowerDataDto> getProductPowerData(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd
    ,@Param("productPowerCd")Integer productPowerCd,@Param("authorCd")String authorCd,@Param("patternCd")Integer patternCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd
    ,@Param("newPriorityOrderCd")Integer newPriorityOrderCd);

}