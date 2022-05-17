package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.entity.vo.PriorityOrderCommodityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassicPriorityOrderCommodityNotMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderCommodityNot> record);

    List<PriorityOrderCommodityVO> selectNotInfo(String companyCd, Integer priorityOrderCd);

    String selectBranchCDForCalcLength(String companyCd);

    int setFinalForWork(String companyCd, Integer priorityOrderCd);

    int deleteFinal(String companyCd, Integer priorityOrderCd);

    int setWorkForFinal(String companyCd, Integer priorityOrderCd);

    List<String> existJan(List<String> janList, String companyCd, Integer priorityOrderCd);
}