package com.trechina.planocycle.mapper;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.vo.PriorityOrderCommodityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderCommodityMustMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderCommodityMust> record);

    List<PriorityOrderCommodityVO> selectMystInfo(String companyCd, Integer priorityOrderCd,String table1,String table2,String janInfoTable);

    void insertPriorityBranchNum(@Param("lists") JSONArray jsonArray,@Param("companyCd")String companyCd, @Param("priorityOrderCd")Integer priorityOrderCd);

    void deletePriorityBranchNum(String companyCd, Integer priorityOrderCd);

    int setFinalForWork(String companyCd, Integer priorityOrderCd);

    int deleteFinal(String companyCd, Integer priorityOrderCd);

    int setWorkForFinal(String companyCd, Integer priorityOrderCd);

    Integer selectCountMustJan(String companyCd, Integer priorityOrderCd, Integer shelfPatternCd);

    List<Map<String, Object>> selectMustJan(String companyCd, Integer priorityOrderCd, String branchList, Integer shelfPatternCd);

    List<Integer> selectPatternByBranch(Integer priorityOrderCd, String companyCd, String branch);

    int selectExistJan(Integer shelfPattern, String jan);
}
