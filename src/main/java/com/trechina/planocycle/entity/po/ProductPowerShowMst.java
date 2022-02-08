package com.trechina.planocycle.entity.po;

public class ProductPowerShowMst {
    private Integer productPowerCd;

    private String conpanyCd;

    private Integer marketPosFlag;

    private String dataCd;

    private Integer showflag;

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public String getConpanyCd() {
        return conpanyCd;
    }

    public void setConpanyCd(String conpanyCd) {
        this.conpanyCd = conpanyCd;
    }

    public Integer getMarketPosFlag() {
        return marketPosFlag;
    }

    public void setMarketPosFlag(Integer marketPosFlag) {
        this.marketPosFlag = marketPosFlag;
    }

    public String getDataCd() {
        return dataCd;
    }

    public void setDataCd(String dataCd) {
        this.dataCd = dataCd == null ? null : dataCd.trim();
    }

    @Override
    public String toString() {
        return "ProductPowerShowMst{" +
                "productPowerCd=" + productPowerCd +
                ", conpanyCd=" + conpanyCd +
                ", marketPosFlag=" + marketPosFlag +
                ", dataCd='" + dataCd + '\'' +
                ", showflag=" + showflag +
                '}';
    }
}
