package com.trechina.planocycle.entity.vo;

public class PriorityAllPatternListVO {
    private String shelfName;
    private Integer shelfPatternCd;
    private String shelfPatternName;
    private Integer taiCnt;
    private Integer tanaCnt;
    private Integer checkFlag;
    private Boolean disabled = true;
    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public String getShelfPatternName() {
        return shelfPatternName;
    }

    public void setShelfPatternName(String shelfPatternName) {
        this.shelfPatternName = shelfPatternName;
    }

    public Integer getTaiCnt() {
        return taiCnt;
    }

    public void setTaiCnt(Integer taiCnt) {
        this.taiCnt = taiCnt;
    }

    public Integer getTanaCnt() {
        return tanaCnt;
    }

    public void setTanaCnt(Integer tanaCnt) {
        this.tanaCnt = tanaCnt;
    }

    public Integer getCheckFlag() {
        return checkFlag;
    }

    public void setCheckFlag(Integer checkFlag) {
        this.checkFlag = checkFlag;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "PriorityAllPatternListVO{" +
                "shelfName='" + shelfName + '\'' +
                ", shelfPatternCd=" + shelfPatternCd +
                ", shelfPatternName='" + shelfPatternName + '\'' +
                ", taiCnt=" + taiCnt +
                ", tanaCnt=" + tanaCnt +
                ", checkFlag=" + checkFlag +
                ", disabled=" + disabled +
                '}';
    }
}
