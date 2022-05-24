package com.trechina.planocycle.entity.dto;

import java.util.LinkedHashMap;
public class PriorityOrderDataDto {
    private String companyCd;
    private Integer productPowerCd;
    private String shelfPatternCd;
    private LinkedHashMap<String,Object> attrList;
    private Integer priorityOrderCd;

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

    public String getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(String shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public LinkedHashMap<String, Object> getAttrList() {
        return attrList;
    }

    public void setAttrList(LinkedHashMap<String, Object> attrList) {
        this.attrList = attrList;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    @Override
    public String toString() {
        return "PriorityOrderDataDto{" +
                "companyCd='" + companyCd + '\'' +
                ", productPowerCd=" + productPowerCd +
                ", shelfPatternCd='" + shelfPatternCd + '\'' +
                ", attrList=" + attrList +
                ", priorityOrderCd=" + priorityOrderCd +
                '}';
    }
}
