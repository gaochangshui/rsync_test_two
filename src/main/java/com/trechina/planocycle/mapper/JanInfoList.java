package com.trechina.planocycle.mapper;


public class JanInfoList {
    private String jan;
    private String companyCd;
    private String commonPartsData;

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

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
        return "JanInfoList{" +
                "jan='" + jan + '\'' +
                ", companyCd='" + companyCd + '\'' +
                ", commonPartsData='" + commonPartsData + '\'' +
                '}';
    }
}
