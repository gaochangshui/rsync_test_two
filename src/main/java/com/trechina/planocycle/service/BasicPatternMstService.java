package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;

import java.util.Map;


public interface BasicPatternMstService {
    Map<String, Object> autoDetect(BasicPatternAutoDetectVO basePatternAutoDetectVO);
    GetCommonPartsDataDto getCommonTableName(String commonPartsData, String companyCd);
}
