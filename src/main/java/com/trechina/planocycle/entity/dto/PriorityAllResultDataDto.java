package com.trechina.planocycle.entity.dto;

public class PriorityAllResultDataDto extends PriorityOrderResultDataDto {
    private Integer shelfPatternCd;
    private Double sales_cnt;
    private Integer priorityAllCd;
    private Integer faceNum;
    private String companyCd;
    private String janCd;

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public Double getSales_cnt() {
        return sales_cnt;
    }

    public void setSales_cnt(Double sales_cnt) {
        this.sales_cnt = sales_cnt;
    }

    public Integer getPriorityAllCd() {
        return priorityAllCd;
    }

    public void setPriorityAllCd(Integer priorityAllCd) {
        this.priorityAllCd = priorityAllCd;
    }

    public Integer getFaceNum() {
        return faceNum;
    }

    public void setFaceNum(Integer faceNum) {
        this.faceNum = faceNum;
    }

    @Override
    public String getCompanyCd() {
        return companyCd;
    }

    @Override
    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    @Override
    public String getJanCd() {
        return janCd;
    }

    @Override
    public void setJanCd(String janCd) {
        this.janCd = janCd;
    }
}
