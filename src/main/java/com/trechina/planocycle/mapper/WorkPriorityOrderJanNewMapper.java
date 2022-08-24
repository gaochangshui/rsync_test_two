package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ZokuseiMst;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkPriorityOrderJanNewMapper {
    List<Map<String, Object>> selectJanNew(Integer priorityOrderCd, List<Integer> allCdList, List<ZokuseiMst> attrList, String proInfoTable,
                                           List<Map<String, Object>> attrHeaderList, String proMstInfoTable);
}
