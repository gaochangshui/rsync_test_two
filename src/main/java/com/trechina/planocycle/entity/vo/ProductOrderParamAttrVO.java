package com.trechina.planocycle.entity.vo;

public class ProductOrderParamAttrVO {
    private String conpanyCd;

    private Integer productPowerCd;

    private String attr;

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

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "ProductOrderParamVO{" +
                "conpanyCd='" + conpanyCd + '\'' +
                ", productPowerCd=" + productPowerCd +
                ", attr='" + attr + '\'' +
                '}';
    }
}
