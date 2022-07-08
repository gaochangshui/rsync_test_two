package com.trechina.planocycle.entity.dto;

import java.util.Map;

public class AttrHeaderSysDto {
    private String tableName;
    /**
     * colIndex：attr1
     */
    private Map<String, String> colNum;

    private String janCdCol;

    private String janNameCol;

    public String getJanCdCol() {
        return janCdCol;
    }

    public void setJanCdCol(String janCdCol) {
        this.janCdCol = janCdCol;
    }

    public String getJanNameCol() {
        return janNameCol;
    }

    public void setJanNameCol(String janNameCol) {
        this.janNameCol = janNameCol;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getColNum() {
        return colNum;
    }

    public void setColNum(Map<String, String> colNum) {
        this.colNum = colNum;
    }
}
