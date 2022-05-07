package com.trechina.planocycle.entity.dto;

import java.util.Arrays;
import java.util.List;

public class ShelfPatternDto {
    private Integer shelfPatternCd;
    private String companyCd;
    private Integer shelfNameCD;
    private List<Integer> area;
    private String shelfPatternName;
    private String ptsRelationID;
    private String commonPartsData;
    private String[] storeCd;

    public String[] getStoreCd() {
        return storeCd;
    }

    public void setStoreCd(String[] storeCd) {
        this.storeCd = storeCd;
    }

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getShelfNameCD() {
        return shelfNameCD;
    }

    public void setShelfNameCD(Integer shelfNameCD) {
        this.shelfNameCD = shelfNameCD;
    }

    public List<Integer> getArea() {
        return area;
    }

    public void setArea(List<Integer> area) {
        this.area = area;
    }

    public String getShelfPatternName() {
        return shelfPatternName;
    }

    public void setShelfPatternName(String shelfPatternName) {
        this.shelfPatternName = shelfPatternName;
    }

    public String getPtsRelationID() {
        return ptsRelationID;
    }

    public void setPtsRelationID(String ptsRelationID) {
        this.ptsRelationID = ptsRelationID;
    }

    public String getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(String commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    @Override
    public String toString() {
        return "ShelfPatternDto{" +
                "shelfPatternCd=" + shelfPatternCd +
                ", companyCd='" + companyCd + '\'' +
                ", shelfNameCD=" + shelfNameCD +
                ", area=" + area +
                ", shelfPatternName='" + shelfPatternName + '\'' +
                ", ptsRelationID='" + ptsRelationID + '\'' +
                ", commonPartsData='" + commonPartsData + '\'' +
                ", storeCd=" + Arrays.toString(storeCd) +
                '}';
    }
}
