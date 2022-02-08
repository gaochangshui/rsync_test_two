package com.trechina.planocycle.entity.vo;

public class ShelfPatternNameVO {
    private Integer shelfPatternCd;
    private String shelfPatternName;

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

    @Override
    public String toString() {
        return "ShelfPatternNameVO{" +
                "shelfPatternCd=" + shelfPatternCd +
                ", shelfPatternName='" + shelfPatternName + '\'' +
                '}';
    }
}
