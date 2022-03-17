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

    List<ProductPowerDataDto> getProductPowerData(@Param("item")WorkPriorityOrderRestrictResult priorityOrderRestrictResults,@Param("companyCd")String companyCd
    ,@Param("productPowerCd")Integer productPowerCd,@Param("authorCd")String authorCd);
}