package com.trechina.planocycle.entity.vo;

public class JanParamVO {
    private CommonPartsDataVO commonPartsData;
    private String janContain;
    private String janKato;
    private String fuzzyQuery;
    private Integer page;
    private Integer pageSize;

    public String getJanContain() {
        return janContain;
    }

    public void setJanContain(String janContain) {
        this.janContain = janContain;
    }

    public String getJanKato() {
        return janKato;
    }

    public void setJanKato(String janKato) {
        this.janKato = janKato;
    }

    public String getFuzzyQuery() {
        return fuzzyQuery;
    }

    public void setFuzzyQuery(String fuzzyQuery) {
        this.fuzzyQuery = fuzzyQuery;
    }

    public CommonPartsDataVO getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(CommonPartsDataVO commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
