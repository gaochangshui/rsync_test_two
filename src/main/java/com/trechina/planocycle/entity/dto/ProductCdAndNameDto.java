package com.trechina.planocycle.entity.dto;

public class ProductCdAndNameDto {
    private String conpanyCd;
    private Integer productPowerCd;
    private String productPowerName;

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

    public String getProductPowerName() {
        return productPowerName;
    }

    public void setProductPowerName(String productPowerName) {
        this.productPowerName = productPowerName;
    }

    @Override
    public String toString() {
        return "ProductCdAndNameDto{" +
                "conpanyCd='" + conpanyCd + '\'' +
                ", productPowerCd=" + productPowerCd +
                ", productPowerName='" + productPowerName + '\'' +
                '}';
    }
}
