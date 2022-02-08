package com.trechina.planocycle.entity.po;

public class ProductPowerReserveMst {
    private String conpanyCd;

    private Integer productPowerCd;

    private Integer marketPosFlag;

    private Integer dataCd;

    private String dataName;

    private Integer showFlag;

    public String getConpanyCd() {
        return conpanyCd;
    }

    public void setConpanyCd(String conpanyCd) {
        this.conpanyCd = conpanyCd;
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

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName == null ? null : dataName.trim();
    }

    public Integer getShowFlag() {
        return showFlag;
    }

    public void setShowFlag(Integer showFlag) {
        this.showFlag = showFlag;
    }
}
