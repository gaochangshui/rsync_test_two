package com.trechina.planocycle.entity.dto;

public class ShelfPtsJoinPatternDto {
    private String companyCd;
    private Integer shelfPtsCd;
    private Integer shelfPatternCd;
    private String startDay;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getShelfPtsCd() {
        return shelfPtsCd;
    }

    public void setShelfPtsCd(Integer shelfPtsCd) {
        this.shelfPtsCd = shelfPtsCd;
    }

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    @Override
    public String toString() {
        return "ShelfPtsJoinPatternDto{" +
                "companyCd='" + companyCd + '\'' +
                ", shelfPtsCd=" + shelfPtsCd +
                ", shelfPatternCd=" + shelfPatternCd +
                ", startDay='" + startDay + '\'' +
                '}';
    }
}
