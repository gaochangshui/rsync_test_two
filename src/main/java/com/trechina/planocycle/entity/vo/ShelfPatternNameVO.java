package com.trechina.planocycle.entity.vo;

public class ShelfPatternNameVO {
    private Integer shelfPatternCd;
    private String shelfPatternName;
    private String storeCd;
    private String storeIsCore;

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

    public String getStoreCd() {
        return storeCd;
    }

    public void setStoreCd(String storeCd) {
        this.storeCd = storeCd;
    }

    public String getStoreIsCore() {
        return storeIsCore;
    }

    public void setStoreIsCore(String storeIsCore) {
        this.storeIsCore = storeIsCore;
    }

    @Override
    public String toString() {
        return "ShelfPatternNameVO{" +
                "shelfPatternCd=" + shelfPatternCd +
                ", shelfPatternName='" + shelfPatternName + '\'' +
                ", storeCd='" + storeCd + '\'' +
                ", storeIsCore='" + storeIsCore + '\'' +
                '}';
    }
}
