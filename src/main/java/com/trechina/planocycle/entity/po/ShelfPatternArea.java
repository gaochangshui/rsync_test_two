package com.trechina.planocycle.entity.po;

public class ShelfPatternArea {
    private String companyCd;

    private Integer shelfPatternCd;

    private Integer areacd;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public Integer getAreacd() {
        return areacd;
    }

    public void setAreacd(Integer areacd) {
        this.areacd = areacd;
    }
}