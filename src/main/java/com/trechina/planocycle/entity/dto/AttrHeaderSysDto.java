package com.trechina.planocycle.entity.dto;

import java.util.List;

public class AttrHeaderSysDto {
    private String tableName;
    private List<String> colNum;

    private String index;

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

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColNum() {
        return colNum;
    }

    public void setColNum(List<String> colNum) {
        this.colNum = colNum;
    }
}
