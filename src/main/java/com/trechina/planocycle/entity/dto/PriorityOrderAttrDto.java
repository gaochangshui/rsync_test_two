package com.trechina.planocycle.entity.dto;

public class PriorityOrderAttrDto {
    private String companyCd;
    private Integer priorityOrderCd;
    private String commonPartsData;

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

    @Override
    public String toString() {
        return "PriorityOrderAttrDto{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", commonPartsData='" + commonPartsData + '\'' +
                '}';
    }
}
