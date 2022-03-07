package com.trechina.planocycle.entity.vo;

public class PriorityOrderAttrListVo {
   private String attrCd;
   private String attrName;
    private String janColSort;
   private Boolean disabled =false;

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

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getJanColSort() {
        return janColSort;
    }

    public void setJanColSort(String janColSort) {
        this.janColSort = janColSort;
    }

    @Override
    public String toString() {
        return "PriorityOrderAttrListVo{" +
                "attrCd=" + attrCd +
                ", attrName='" + attrName + '\'' +
                ", janColSort='" + janColSort + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
