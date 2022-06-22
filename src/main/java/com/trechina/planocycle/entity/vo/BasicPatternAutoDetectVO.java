package com.trechina.planocycle.entity.vo;

public class BasicPatternAutoDetectVO {
    private String companyCd;
    private Integer basicPatternCd;
    private String commonPartsData;
    private Integer shelfPatternCd;
    private String attrList;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getBasicPatternCd() {
        return basicPatternCd;
    }

    public void setBasicPatternCd(Integer basicPatternCd) {
        this.basicPatternCd = basicPatternCd;
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

    public String getAttrList() {
        return attrList;
    }

    public void setAttrList(String attrList) {
        this.attrList = attrList;
    }
}
