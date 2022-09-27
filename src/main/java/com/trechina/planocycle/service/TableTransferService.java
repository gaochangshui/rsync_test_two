package com.trechina.planocycle.service;

public interface TableTransferService {
    int getAreasTransfer();

    int getBranchsTransfer();

    int getJansTransfer();

    int getAttrTransfer();

    int getJanInfoTransfer();

    void syncZokuseiMst();

}
