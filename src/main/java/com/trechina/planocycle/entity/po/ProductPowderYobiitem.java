package com.trechina.planocycle.entity.po;

public class ProductPowderYobiitem {
    private String companyCd;
    private Long productPowerCd;
    private Integer itemCd;
    private String itemName;
    private String filePath;

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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
