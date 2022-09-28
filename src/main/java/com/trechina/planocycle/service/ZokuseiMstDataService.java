package com.trechina.planocycle.service;

import java.util.List;
import java.util.Map;

public interface ZokuseiMstDataService {
    void syncZokuseiMstData(String companyCd, String classCd);

    void setZokuseiData(String company, String classCd, Integer zokuseiId, Integer col, List<Map<String, Object>> headerMap);
}
