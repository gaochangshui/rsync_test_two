package com.trechina.planocycle.service;

import java.util.Map;

public interface PriorityAllPtsService {

    void saveWorkPtsData(String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd);

    Map<String, Object> getPtsDetailData(Integer patternCd, String companyCd, Integer priorityAllCd);
}
