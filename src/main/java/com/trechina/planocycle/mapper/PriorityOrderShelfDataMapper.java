package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictJanDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderShelfDataMapper {

    List<PriorityOrderRestrictDto> getRestrictData(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd
            ,@Param("priorityOrderCd")Integer priorityOrderCd,@Param("tanaWidth") Integer tanaWidth);

    List<PriorityOrderPlatformShedDto> getPlatformShedData(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    List<PriorityOrderRestrictJanDto> getRestrictJans(@Param("item") PriorityOrderRestDto priorityOrderRestDto, @Param("authorCd")String authorCd);

    List<PriorityOrderRestrictJanDto> getPlatformShedJans(@Param("item")PriorityOrderPlatformShedDto priorityOrderPlatformShedDto,@Param("authorCd")String authorCd);

   // List<>
}
