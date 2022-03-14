package com.trechina.planocycle.entity.po;

public class WorkPriorityOrderSort {
    private String companyCd;
    private Integer zokuseiId;
    private String zokuseiName;
    private Integer sortNum;
    private Boolean disabled = false;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getZokuseiId() {
        return zokuseiId;
    }

    public void setZokuseiId(Integer zokuseiId) {
        this.zokuseiId = zokuseiId;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public String getZokuseiName() {
        return zokuseiName;
    }

    public void setZokuseiName(String zokuseiName) {
        this.zokuseiName = zokuseiName;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "WorkPriorityOrderSort{" +
                "companyCd='" + companyCd + '\'' +
                ", zokuseiId='" + zokuseiId + '\'' +
                ", zokuseiName='" + zokuseiName + '\'' +
                ", sortNum=" + sortNum +
                ", disabled=" + disabled +
                '}';
    }
}
