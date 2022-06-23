package com.trechina.planocycle.entity.dto;

public class PriorityOrderAttrDto {
    private String companyCd;
    private Integer priorityOrderCd;
    private String commonPartsData;
    private Integer ShelfPatternCd;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public String getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(String commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    public Integer getShelfPatternCd() {
        return ShelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        ShelfPatternCd = shelfPatternCd;
    }

    @Override
    public String toString() {
        return "PriorityOrderAttrDto{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", commonPartsData='" + commonPartsData + '\'' +
                ", ShelfPatternCd=" + ShelfPatternCd +
                '}';
    }
}
