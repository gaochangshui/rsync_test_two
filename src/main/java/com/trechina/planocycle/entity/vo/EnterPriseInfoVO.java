package com.trechina.planocycle.entity.vo;

public class EnterPriseInfoVO {
    private String conpanyCd;

    private String productPowerName;

    public String getConpanyCd() {
        return conpanyCd;
    }

    public void setConpanyCd(String conpanyCd) {
        this.conpanyCd = conpanyCd;
    }

    public String getProductPowerName() {
        return productPowerName;
    }

    public void setProductPowerName(String productPowerName) {
        this.productPowerName = productPowerName;
    }

    @Override
    public String toString() {
        return "EnterPriseInfoVO{" +
                "conpanyCd=" + conpanyCd +
                ", productPowerName='" + productPowerName + '\'' +
                '}';
    }

}
