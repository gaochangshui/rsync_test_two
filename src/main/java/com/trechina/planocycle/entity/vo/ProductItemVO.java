package com.trechina.planocycle.entity.vo;

public class ProductItemVO {
    private String itemCd;
    private String itemName;
    private Integer requiredType;
    private Integer itemType;

    public String getItemCd() {
        return itemCd;
    }

    public void setItemCd(String itemCd) {
        this.itemCd = itemCd;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getRequiredType() {
        return requiredType;
    }

    public void setRequiredType(Integer requiredType) {
        this.requiredType = requiredType;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }
}
