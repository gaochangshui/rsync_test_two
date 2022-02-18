package com.trechina.planocycle.entity.po;

import java.math.BigDecimal;

public class ProductPowderYobiitemData {
    private String companyCd;
    private Long productPowerCd;
    private Integer itemCd;
    private String jan;
    private BigDecimal value;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Long getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Long productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public Integer getItemCd() {
        return itemCd;
    }

    public void setItemCd(Integer itemCd) {
        this.itemCd = itemCd;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
