package com.trechina.planocycle.entity.po;

public class Zokusei {
    private String companyCd;
    private String classCd;
    private Integer zokuseiId;
    private String zokuseiNm;
    private Integer zokuseiSort;
    private Integer type;
    private Integer sortType;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getClassCd() {
        return classCd;
    }

    public void setClassCd(String classCd) {
        this.classCd = classCd;
    }

    public Integer getZokuseiId() {
        return zokuseiId;
    }

    public void setZokuseiId(Integer zokuseiId) {
        this.zokuseiId = zokuseiId;
    }

    public String getZokuseiNm() {
        return zokuseiNm;
    }

    public void setZokuseiNm(String zokuseiNm) {
        this.zokuseiNm = zokuseiNm;
    }

    public Integer getZokuseiSort() {
        return zokuseiSort;
    }

    public void setZokuseiSort(Integer zokuseiSort) {
        this.zokuseiSort = zokuseiSort;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }
}
