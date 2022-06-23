package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;

public interface BasicPatternMstService {
    GetCommonPartsDataDto getCommonTableName(String commonPartsData, String companyCd );
}
