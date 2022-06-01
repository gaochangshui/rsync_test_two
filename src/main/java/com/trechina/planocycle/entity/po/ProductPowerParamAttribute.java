package com.trechina.planocycle.entity.po;

public class ProductPowerParamAttribute {
    private String companyCd;

    private Integer productPowerCd;

    private Integer attrCd;

    private Integer attrValue;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public Integer getAttrCd() {
        return attrCd;
    }

    public void setAttrCd(Integer attrCd) {
        this.attrCd = attrCd;
    }

    public Integer getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(Integer attrValue) {
        this.attrValue = attrValue;
    }

    @Override
    public String toString() {
        return "ProductPowerParamAttribute{" +
                "companyCd='" + companyCd + '\'' +
                ", productPowerCd=" + productPowerCd +
                ", attrCd=" + attrCd +
                ", attrValue='" + attrValue + '\'' +
                '}';
    }
}
