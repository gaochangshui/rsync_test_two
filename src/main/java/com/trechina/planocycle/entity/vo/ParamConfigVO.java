package com.trechina.planocycle.entity.vo;

public class ParamConfigVO {
    private String itemName;
    private String itemCd;
    private String itemValue;
    private Integer flag;

    private Integer rankFlag;

    public Integer getRankFlag() {
        return rankFlag;
    }

    public void setRankFlag(Integer rankFlag) {
        this.rankFlag = rankFlag;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCd() {
        return itemCd;
    }

    public void setItemCd(String itemCd) {
        this.itemCd = itemCd;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
