package com.trechina.planocycle.entity.po;


import java.math.BigDecimal;

public class WKYobiiiternData {
    private BigDecimal dataValue;
    private String jan;
    private String dataName;
    private Integer dataSort;
    private Integer dataCd;

    public BigDecimal getDataValue() {
        return dataValue;
    }

    public void setDataValue(BigDecimal dataValue) {
        this.dataValue = dataValue;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getDataSort() {
        return dataSort;
    }

    public void setDataSort(Integer dataSort) {
        this.dataSort = dataSort;
    }

    public Integer getDataCd() {
        return dataCd;
    }

    @Override
    public String toString() {
        return "WKYobiiiternData{" +
                "dataValue=" + dataValue +
                ", jan='" + jan + '\'' +
                ", dataName='" + dataName + '\'' +
                ", dataSort=" + dataSort +
                ", dataCd=" + dataCd +
                '}';
    }

    public void setDataCd(Integer dataCd) {
        this.dataCd = dataCd;

    }
}
