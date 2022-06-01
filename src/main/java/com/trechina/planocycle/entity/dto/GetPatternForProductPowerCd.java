package com.trechina.planocycle.entity.dto;

public class GetPatternForProductPowerCd {
    private Object shelfPatternCd;
    private Integer shelfNameCd;

    public Object getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Object shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public Integer getShelfName() {
        return shelfNameCd;
    }

    public void setShelfName(Integer shelfName) {
        this.shelfNameCd = shelfName;
    }

    @Override
    public String toString() {
        return "GetPatternForProductPowerCd{" +
                "shelfPatternCd=" + shelfPatternCd +
                ", shelfName=" + shelfNameCd +
                '}';
    }
}
