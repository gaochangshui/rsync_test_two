package com.trechina.planocycle.entity.dto;

public class ShelfPtsDataDto {
    private Integer shelfPatternCd;
    private Integer id;

    private String shelfPatternName;

    public String getShelfPatternName() {
        return shelfPatternName;
    }

    public void setShelfPatternName(String shelfPatternName) {
        this.shelfPatternName = shelfPatternName;
    }

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
