package com.trechina.planocycle.entity.dto;

public class ProductCdAndNameDto {
    private String conpanyCd;
    private Integer productPowerNo;
    private String productPowerName;

    public String getConpanyCd() {
        return conpanyCd;
    }

    public void setConpanyCd(String conpanyCd) {
        this.conpanyCd = conpanyCd;
    }

    public Integer getProductPowerNo() {
        return productPowerNo;
    }

    public void setProductPowerNo(Integer productPowerNo) {
        this.productPowerNo = productPowerNo;
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
                ", productPowerNo=" + productPowerNo +
                ", productPowerName='" + productPowerName + '\'' +
                '}';
    }
}
