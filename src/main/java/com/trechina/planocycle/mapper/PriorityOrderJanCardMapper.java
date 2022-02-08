package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderJanCard;
import com.trechina.planocycle.entity.vo.PriorityOrderJanCardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderJanCardMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderJanCardVO> selectJanCard(String companyCd, Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderJanCard> record);
}
