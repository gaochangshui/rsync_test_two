package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictJanDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderShelfDataMapper {

    List<PriorityOrderRestrictDto> getRestrictData(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd);

    List<PriorityOrderPlatformShedDto> getPlatformShedData(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd);

    List<PriorityOrderRestrictJanDto> getRestrictJans(@Param("item")PriorityOrderRestrictDto priorityOrderRestrictDto);

    List<PriorityOrderRestrictJanDto> getPlatformShedJans(@Param("item")PriorityOrderPlatformShedDto priorityOrderPlatformShedDto);

   // List<>
}
