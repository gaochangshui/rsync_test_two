package com.trechina.planocycle.mapper;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.vo.PriorityOrderCommodityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderCommodityMustMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderCommodityMust> record);

    List<PriorityOrderCommodityVO> selectMystInfo(String companyCd, Integer priorityOrderCd);

    void insertPriorityBranchNum(@Param("lists") JSONArray jsonArray,@Param("companyCd")String companyCd, @Param("priorityOrderCd")Integer priorityOrderCd);

    void deletePriorityBranchNum(String companyCd, Integer priorityOrderCd);

    int setFinalForWork(String companyCd, Integer priorityOrderCd);

    int deleteFinal(String companyCd, Integer priorityOrderCd);

    int setWorkForFinal(String companyCd, Integer priorityOrderCd);
}
