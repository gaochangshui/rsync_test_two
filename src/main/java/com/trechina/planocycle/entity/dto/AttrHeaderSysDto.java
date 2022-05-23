package com.trechina.planocycle.entity.dto;

import java.util.List;

public class AttrHeaderSysDto {
    private String tableName;
    private List<String> colNum;

    private Integer index;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
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
