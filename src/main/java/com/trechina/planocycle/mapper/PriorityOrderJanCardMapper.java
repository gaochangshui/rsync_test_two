package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderJanCard;
import com.trechina.planocycle.entity.vo.PriorityOrderJanCardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderJanCardMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int workDelete(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderJanCardVO> selectJanCard(String companyCd, Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderJanCard> record);
    //将最终表数据转到临时表
    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
}
