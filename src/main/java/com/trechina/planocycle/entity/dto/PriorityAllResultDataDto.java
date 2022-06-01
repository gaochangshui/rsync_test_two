package com.trechina.planocycle.entity.dto;

public class PriorityAllResultDataDto extends PriorityOrderResultDataDto {
    private Integer shelfPatternCd;
    private Double salesCnt;
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

    @Override
    public Double getSalesCnt() {
        return salesCnt;
    }

    @Override
    public void setSalesCnt(Double salesCnt) {
        this.salesCnt = salesCnt;
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

    @Override
    public String toString() {
        return "PriorityAllResultDataDto{" +
                "shelfPatternCd=" + shelfPatternCd +
                ", salesCnt=" + salesCnt +
                ", priorityAllCd=" + priorityAllCd +
                ", faceNum=" + faceNum +
                ", companyCd='" + companyCd + '\'' +
                ", janCd='" + janCd + '\'' +
                '}';
    }
}
