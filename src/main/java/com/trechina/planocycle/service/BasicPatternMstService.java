package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.po.BasicPatternRestrictRelation;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;

import java.util.Map;


public interface BasicPatternMstService {
    Map<String, Object> autoDetect(BasicPatternAutoDetectVO basePatternAutoDetectVO);
    GetCommonPartsDataDto getCommonTableName(String commonPartsData, String companyCd);

    Map<String, Object> getAttrDisplay(String companyCd, Integer priorityOrderCd);

    Map<String, Object> autoCalculation(String companyCd, Integer priorityOrderCd, Integer partition);

    Map<String, Object> setAttrDisplay( BasicPatternRestrictRelation basicPatternRestrictRelation);

    Map<String, Object> autoTaskId(String taskId);
}
