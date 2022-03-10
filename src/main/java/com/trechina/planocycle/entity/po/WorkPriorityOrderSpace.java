package com.trechina.planocycle.entity.po;

public class WorkPriorityOrderSpace {
    private String companyCd;

    private String authorCd;

    private String attribute1Value;

    private String attribute2Value;

    private Integer oldZoning;

    private Integer newZoning;

    private Integer tanaCount;

    private Integer zoningNum;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd == null ? null : authorCd.trim();
    }

    public String getAttribute1Value() {
        return attribute1Value;
    }

    public void setAttribute1Value(String attribute1Value) {
        this.attribute1Value = attribute1Value == null ? null : attribute1Value.trim();
    }

    public String getAttribute2Value() {
        return attribute2Value;
    }

    public void setAttribute2Value(String attribute2Value) {
        this.attribute2Value = attribute2Value == null ? null : attribute2Value.trim();
    }

    public Integer getOldZoning() {
        return oldZoning;
    }

    public void setOldZoning(Integer oldZoning) {
        this.oldZoning = oldZoning;
    }

    public Integer getNewZoning() {
        return newZoning;
    }

    public void setNewZoning(Integer newZoning) {
        this.newZoning = newZoning;
    }

    public Integer getTanaCount() {
        return tanaCount;
    }

    public void setTanaCount(Integer tanaCount) {
        this.tanaCount = tanaCount;
    }

    public Integer getZoningNum() {
        return zoningNum;
    }

    public void setZoningNum(Integer zoningNum) {
        this.zoningNum = zoningNum;
    }
}