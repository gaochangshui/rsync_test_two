package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ProductPowerMstVo {
    private String productPowerName;
    private String authorName;
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy/MM/dd hh:mm" )
    private Date registered;
    private Integer sku;
    private Integer noRestrictionNum;

    public String getProductPowerName() {
        return productPowerName;
    }

    public void setProductPowerName(String productPowerName) {
        this.productPowerName = productPowerName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public Integer getSku() {
        return sku;
    }

    public void setSku(Integer sku) {
        this.sku = sku;
    }

    public Integer getNoRestrictionNum() {
        return noRestrictionNum;
    }

    public void setNoRestrictionNum(Integer noRestrictionNum) {
        this.noRestrictionNum = noRestrictionNum;
    }

    @Override
    public String toString() {
        return "ProductPowerMstVo{" +
                "productPowerName='" + productPowerName + '\'' +
                ", authorName='" + authorName + '\'' +
                ", registered=" + registered +
                ", sku=" + sku +
                ", noRestrictionNum=" + noRestrictionNum +
                '}';
    }
}
