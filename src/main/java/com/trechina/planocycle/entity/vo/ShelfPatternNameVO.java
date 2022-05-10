package com.trechina.planocycle.entity.vo;

public class ShelfPatternNameVO {
    private Integer shelfPatternCd;
    private String shelfPatternName;
    private String storeCd;

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

    @Override
    public String toString() {
        return "ShelfPatternNameVO{" +
                "shelfPatternCd=" + shelfPatternCd +
                ", shelfPatternName='" + shelfPatternName + '\'' +
                ", storeCd='" + storeCd + '\'' +
                '}';
    }
}
