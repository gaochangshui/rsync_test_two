package com.trechina.planocycle.entity.po;

import java.math.BigDecimal;

public class WorkProductPowerReserveData {
    private String companyCd;
    private String authorCd;
    private Integer dataCd;
    private String jan;
    private BigDecimal dataValue;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd;
    }

    public Integer getDataCd() {
        return dataCd;
    }

    public void setDataCd(Integer dataCd) {
        this.dataCd = dataCd;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public BigDecimal getDataValue() {
        return dataValue;
    }

    public void setDataValue(BigDecimal dataValue) {
        this.dataValue = dataValue;
    }
}
