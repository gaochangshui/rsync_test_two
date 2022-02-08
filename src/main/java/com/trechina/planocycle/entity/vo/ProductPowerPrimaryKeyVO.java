package com.trechina.planocycle.entity.vo;

public class ProductPowerPrimaryKeyVO {
    private String companyCd;
    private Integer productPowerCd;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    @Override
    public String toString() {
        return "ProductPowerPrimaryKeyVO{" +
                "companyCd='" + companyCd + '\'' +
                ", productPowerCd=" + productPowerCd +
                '}';
    }
}
