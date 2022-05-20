package com.trechina.planocycle.entity.dto;


public class EnterpriseAxisDto {
    private String companyCd;
    private String commonPartsData;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(String commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    @Override
    public String toString() {
        return "EnterpriseAxisDto{" +
                "companyCd='" + companyCd + '\'' +
                ", commonPartsData='" + commonPartsData + '\'' +
                '}';
    }
}
