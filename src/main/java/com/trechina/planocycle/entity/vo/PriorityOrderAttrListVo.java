package com.trechina.planocycle.entity.vo;

public class PriorityOrderAttrListVo {
    private String attrCd;
    private String attrName;
    private String type;
    private String jansColNm;
    private String janColSort;
    private Boolean disabled = false;

    public String getAttrCd() {
        return attrCd;
    }

    public void setAttrCd(String attrCd) {
        this.attrCd = attrCd;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJansColNm() {
        return jansColNm;
    }

    public void setJansColNm(String jansColNm) {
        this.jansColNm = jansColNm;
    }

    public String getJanColSort() {
        return janColSort;
    }

    public void setJanColSort(String janColSort) {
        this.janColSort = janColSort;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "PriorityOrderAttrListVo{" +
                "attrCd='" + attrCd + '\'' +
                ", attrName='" + attrName + '\'' +
                ", type='" + type + '\'' +
                ", jansColNm='" + jansColNm + '\'' +
                ", janColSort='" + janColSort + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
