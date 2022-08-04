package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityAllSaveDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface BasicAllPatternMstService {
    Map<String, Object> autoCalculation(PriorityAllSaveDto priorityAllSaveDto);

    @Transactional(rollbackFor = Exception.class)
    Integer saveWKAllPatternData(PriorityAllSaveDto priorityAllSaveDto);


    void autoDetect(String companyCd,Integer priorityAllCd,Integer patternCd,Integer priorityOrderCd,String aud);
}
