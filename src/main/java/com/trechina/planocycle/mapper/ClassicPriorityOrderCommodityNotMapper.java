package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderBranchNumDto;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.entity.vo.CommodityBranchPrimaryKeyVO;
import com.trechina.planocycle.entity.vo.CommodityBranchVO;
import com.trechina.planocycle.entity.vo.PriorityOrderCommodityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderCommodityNotMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderCommodityNot> record);

    List<PriorityOrderCommodityVO> selectNotInfo(String companyCd, Integer priorityOrderCd,String table1,String table2,String janInfoTable);

    String selectBranchCDForCalcLength(String companyCd);

    int setFinalForWork(String companyCd, Integer priorityOrderCd);

    int deleteFinal(String companyCd, Integer priorityOrderCd);

    int setWorkForFinal(String companyCd, Integer priorityOrderCd);

    List<String> existJan(List<String> janList, String companyCd, Integer priorityOrderCd);

    List<Map<String, Object>> selectNotJan(String companyCd, Integer priorityOrderCd, String branchList, Integer shelfPatternCd);

    int selectCountNotJan(String companyCd, Integer priorityOrderCd, Integer shelfPatternCd);

    List<Map<String, Object>> getPriorityOrderNotList(String companyCd, Integer priorityOrderCd, List<String> attrList);

    List<PriorityOrderBranchNumDto> getBranchAndPattern(String janNew, String companyCd,Integer priorityOrderCd,String table1,String table2);

    List<CommodityBranchVO> getExistCommodityNotBranchList(String companyCd,Integer priorityOrderCd, String jan,String table1,String table2);

    void updateFlag(String companyCd, Integer priorityOrderCd, String jan, List<Map<String, Object>> list);

    void delCommodityNotBranch(CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO);
}
