package com.trechina.planocycle.entity.dto;

import lombok.Data;

import java.util.Map;
@Data
public class AttrHeaderSysDto {
    private String tableName;
    /**
     * colIndex：attr1
     */
    private Map<String, String> colNum;

    private String janCdCol;

    private String janNameCol;

}
