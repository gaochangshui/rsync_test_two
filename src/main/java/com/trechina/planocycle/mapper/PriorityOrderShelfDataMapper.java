package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictJanDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderShelfDataMapper {

    List<PriorityOrderPlatformShedDto> getPlatformShedData(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);
    List<PriorityOrderPlatformShedDto> getNewPlatformShedData(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    List<PriorityOrderRestrictJanDto> getRestrictJans(@Param("item") PriorityOrderRestDto priorityOrderRestDto, @Param("authorCd")String authorCd);

    List<PriorityOrderRestrictJanDto> getPlatformShedJans(@Param("item")PriorityOrderPlatformShedDto priorityOrderPlatformShedDto,@Param("authorCd")String authorCd);

    void updateFaceNum(@Param("item")  Map<String,Object> map,@Param("id")Integer id,@Param("tableName")String tableName);

    Integer selectRegclass();

    void delJan(@Param("item") Map<String, Object> map,@Param("id")Integer id,@Param("tableName")String tableName);

    List<Map<String,Object>> getAlikeTana(@Param("item") Map<String, Object> map,@Param("id")Integer id,@Param("tableName")String tableName,@Param("ptsFlag")Integer ptsFlag);

    void updatePositionCd(@Param("list") List<Map<String, Object>> alikeTana,@Param("id") Integer id,@Param("tableName")String tableName);

    void insertPosition(@Param("list") List<Map<String, Object>> alikeTana,@Param("id") Integer id,@Param("tableName")String tableName
    ,@Param("ptsFlag")Integer ptsFlag);

    void delTana(@Param("item") Map<String, Object> map,@Param("id")Integer id,@Param("tableName")String tableName);
}
