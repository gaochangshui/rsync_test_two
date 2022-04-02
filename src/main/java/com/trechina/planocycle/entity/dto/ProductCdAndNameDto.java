package com.trechina.planocycle.entity.dto;

public class ProductCdAndNameDto {
    private String companyCd;
    private Integer productPowerNo;
    private String productPowerName;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
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
                "companyCd='" + companyCd + '\'' +
                ", productPowerNo=" + productPowerNo +
                ", productPowerName='" + productPowerName + '\'' +
                '}';
    }
}
