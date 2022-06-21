package com.trechina.planocycle.entity.vo;

public class AutoDetectVo {
    private String companyCd;
    private Integer priorityOrderCd;
    private String commonPartsData;
    private Integer shelfPatternCd;
    private Short attribute1;

    private Short attribute2;

    private Integer isAutoDetect;

    public Integer getIsAutoDetect() {
        return isAutoDetect;
    }

    public void setIsAutoDetect(Integer isAutoDetect) {
        this.isAutoDetect = isAutoDetect;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public String getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(String commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public Short getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(Short attribute1) {
        this.attribute1 = attribute1;
    }

    public Short getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(Short attribute2) {
        this.attribute2 = attribute2;
    }
}
