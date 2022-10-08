package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderCompareJanData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderCompareJanDataMapper {

    List<PriorityOrderCompareJanData> getPatternCompare(String companyCd,Integer priorityOrderCd,String tableName,List<String> groupCompany);

    List<Integer> getPatternList(String companyCd, Integer priorityOrderCd);

    List<Map<String,Object>> getChangeJan(String companyCd, Integer priorityOrderCd);
}
