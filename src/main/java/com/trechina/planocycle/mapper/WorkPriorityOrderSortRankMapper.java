package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderJanNewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderSortRankMapper {

    int delete(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int insert(@Param("companyCd")String companyCd, @Param("list")List<PriorityOrderJanNewDto> list, @Param("authorCd")String authorCd
    , @Param("priorityOrderCd")Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd")String authorCd);

}
