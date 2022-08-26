package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.BasicPatternRestrictRelation;
import com.trechina.planocycle.entity.po.BasicPatternRestrictResult;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;


public interface BasicPatternMstService {
    Map<String, Object> autoDetect(BasicPatternAutoDetectVO basePatternAutoDetectVO);
    GetCommonPartsDataDto getCommonTableName(String commonPartsData, String companyCd);

    Map<String, Object> getAttrDisplay(String companyCd, Integer priorityOrderCd);

    Map<String, Object> autoCalculation(String companyCd, Integer priorityOrderCd, Integer partition, Integer heightSpace, Integer tanaWidthCheck);

    Map<String, Object> setAttrDisplay( BasicPatternRestrictRelation basicPatternRestrictRelation);

    Map<String, Object> autoTaskId(String taskId);

    Map<String, Object> preCalculation(String companyCd, Long patternCd, Integer priorityOrderCd);

    Map<String, BasicPatternRestrictResult> getJanInfoClassify(List<Map<String, Object>> classifyList,
                                                               String companyCd, List<ZokuseiMst> zokuseiMst, String authorCd, Long priorityOrderCd);

    /**
     * jan长宽高修改
     */
    List<PriorityOrderResultDataDto> updateJanSize( List<PriorityOrderResultDataDto> priorityOrderResultDataDtoList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    List<Map<String, Object>> updateJanSizeByMap( List<Map<String, Object>> priorityOrderResultDataDtoList);
}
