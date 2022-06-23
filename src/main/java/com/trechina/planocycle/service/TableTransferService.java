package com.trechina.planocycle.service;

import java.util.List;
import java.util.Map;

public interface TableTransferService {
    int getAreasTransfer();

    int getBranchsTransfer();

    int getJansTransfer();

    int getAttrTransfer();

    int getJanInfoTransfer();

    void syncZokuseiMst();

    void setZokuseiData(String company,String classCd,Integer zokuseiId,Integer col, List<Map<String, Object>> cdList);

}
