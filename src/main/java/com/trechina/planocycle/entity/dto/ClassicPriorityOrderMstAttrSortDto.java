package com.trechina.planocycle.entity.dto;

public class ClassicPriorityOrderMstAttrSortDto {
    private String attrName;
    private String attrCd;

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrCd() {
        return attrCd;
    }

    public void setAttrCd(String attrCd) {
        this.attrCd = attrCd;
    }
}
