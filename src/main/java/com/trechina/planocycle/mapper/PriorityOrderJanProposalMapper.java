package com.trechina.planocycle.mapper;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.po.PriorityOrderJanProposal;
import com.trechina.planocycle.entity.vo.PriorityOrderJanProposalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderJanProposalMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") JSONArray jsonArray,@Param("companyCd") String companyCd,@Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderJanProposalVO> selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int updateByPrimaryKey(@Param("lists") List<PriorityOrderJanProposal> record);
}
