package com.trechina.planocycle.entity.vo;

public class WorkPriorityOrderSortVo {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer attrCd;
    private String attrName;
    private Integer sortNum;
    private Boolean disabled = false;
    private Boolean backgroundColor = false;

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public Integer getAttrCd() {
        return attrCd;
    }

    public void setAttrCd(Integer attrCd) {
        this.attrCd = attrCd;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Boolean backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    @Override
    public String toString() {
        return "WorkPriorityOrderSortVo{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", attrCd=" + attrCd +
                ", attrName='" + attrName + '\'' +
                ", sortNum=" + sortNum +
                ", disabled=" + disabled +
                ", backgroundColor=" + backgroundColor +
                '}';
    }
}