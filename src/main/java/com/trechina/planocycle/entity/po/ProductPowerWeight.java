package com.trechina.planocycle.entity.po;

import java.math.BigDecimal;

public class ProductPowerWeight {
    private String conpanyCd;

    private Integer productPowerCd;

    private Integer marketPosFlag;

    private Integer dataCd;

    private BigDecimal dataWeight;

    public String getConpanyCd() {
        return conpanyCd;
    }

    public void setConpanyCd(String conpanyCd) {
        this.conpanyCd = conpanyCd == null ? null : conpanyCd.trim();
    }

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public Integer getMarketPosFlag() {
        return marketPosFlag;
    }

    public void setMarketPosFlag(Integer marketPosFlag) {
        this.marketPosFlag = marketPosFlag;
    }

    public Integer getDataCd() {
        return dataCd;
    }

    public void setDataCd(Integer dataCd) {
        this.dataCd = dataCd;
    }

    public BigDecimal getDataWeight() {
        return dataWeight;
    }

    public void setDataWeight(BigDecimal dataWeight) {
        this.dataWeight = dataWeight;
    }
}