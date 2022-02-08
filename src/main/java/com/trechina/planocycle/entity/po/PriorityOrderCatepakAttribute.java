package com.trechina.planocycle.entity.po;

public class PriorityOrderCatepakAttribute {
    private Integer attrCd;

    private Integer catepakCd;

    private String companyCd;

    private Integer priorityOrderCd;

    private String attrValue;

    private Integer flg;

    public Integer getAttrCd() {
        return attrCd;
    }

    public void setAttrCd(Integer attrCd) {
        this.attrCd = attrCd;
    }

    public Integer getCatepakCd() {
        return catepakCd;
    }

    public void setCatepakCd(Integer catepakCd) {
        this.catepakCd = catepakCd;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue == null ? null : attrValue.trim();
    }

    public Integer getFlg() {
        return flg;
    }

    public void setFlg(Integer flg) {
        this.flg = flg;
    }
}