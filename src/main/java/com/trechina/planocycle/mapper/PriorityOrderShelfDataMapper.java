package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPtsDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictJanDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderShelfDataMapper {

    List<PriorityOrderPlatformShedDto> getPlatformShedData(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);
    List<PriorityOrderPlatformShedDto> getNewPlatformShedData(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    List<PriorityOrderRestrictJanDto> getRestrictJans(@Param("item") PriorityOrderRestDto priorityOrderRestDto, @Param("authorCd")String authorCd);

    List<PriorityOrderRestrictJanDto> getPlatformShedJans(@Param("item")PriorityOrderPlatformShedDto priorityOrderPlatformShedDto,@Param("authorCd")String authorCd);

    void updateFaceNum(@Param("item") PriorityOrderPtsDto shelfPtsDataJandata);

    // List<>
}
