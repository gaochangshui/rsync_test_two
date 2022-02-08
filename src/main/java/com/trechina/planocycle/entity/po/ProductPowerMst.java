package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ProductPowerMst {
    private String conpanyCd;

    private Integer productPowerCd;

    private String productPowerName;

    private String authorCd;

    private String authorName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date registered;

    private String maintainerCd;

    private String maintainerName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date modified;

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
        this.productPowerName = productPowerName == null ? null : productPowerName.trim();
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName == null ? null : authorName.trim();
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public String getMaintainerCd() {
        return maintainerCd;
    }

    public void setMaintainerCd(String maintainerCd) {
        this.maintainerCd = maintainerCd;
    }

    public String getMaintainerName() {
        return maintainerName;
    }

    public void setMaintainerName(String maintainerName) {
        this.maintainerName = maintainerName == null ? null : maintainerName.trim();
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "ProductPowerMst{" +
                "conpanyCd='" + conpanyCd + '\'' +
                ", productPowerCd=" + productPowerCd +
                ", productPowerName='" + productPowerName + '\'' +
                ", authorCd=" + authorCd +
                ", authorName='" + authorName + '\'' +
                ", registered=" + registered +
                ", maintainerCd=" + maintainerCd +
                ", maintainerName='" + maintainerName + '\'' +
                ", modified=" + modified +
                '}';
    }
}
