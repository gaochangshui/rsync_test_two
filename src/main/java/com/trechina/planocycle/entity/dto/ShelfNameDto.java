package com.trechina.planocycle.entity.dto;

import java.util.List;

public class ShelfNameDto {
    private String companyCd;
    private String shelfName;
    private List<Integer> area;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }

    public List<Integer> getArea() {
        return area;
    }

    public void setArea(List<Integer> area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "ShelfNameDto{" +
                "companyCd='" + companyCd + '\'' +
                ", shelfName='" + shelfName + '\'' +
                ", area=" + area +
                '}';
    }
}
