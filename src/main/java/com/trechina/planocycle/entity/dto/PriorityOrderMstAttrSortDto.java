package com.trechina.planocycle.entity.dto;

public class PriorityOrderMstAttrSortDto {
    private String value;
    private String sort;
    private Integer rankSort;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getRankSort() {
        return rankSort;
    }

    public void setRankSort(Integer rankSort) {
        this.rankSort = rankSort;
    }
}
