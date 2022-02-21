package com.trechina.planocycle.entity.vo;

public class CommodityListInfoVO {
    private Integer productPowerCd;

    private String productPowerName;





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
        return "CommodityListInfoVO{" +
                "productPowerCd=" + productPowerCd +
                ", productPowerName='" + productPowerName + '\'' +
                '}';
    }
}
