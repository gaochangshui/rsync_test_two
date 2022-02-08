package com.trechina.planocycle.entity.po;

public class ShelfNameArea {
    private Integer shelfNameCd;

    private Integer areacd;

    private String companyCd;

    public Integer getShelfNameCd() {
        return shelfNameCd;
    }

    public void setShelfNameCd(Integer shelfNameCd) {
        this.shelfNameCd = shelfNameCd;
    }

    public Integer getAreacd() {
        return areacd;
    }

    public void setAreacd(Integer areacd) {
        this.areacd = areacd;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    @Override
    public String toString() {
        return "ShelfNameArea{" +
                "shelfNameCd=" + shelfNameCd +
                ", areacd=" + areacd +
                ", companyCd='" + companyCd + '\'' +
                '}';
    }
}
