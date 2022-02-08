package com.trechina.planocycle.entity.po;

public class PriorityOrderMst {
    private String companyCd;

    private Integer priorityOrderCd;

    private String priorityOrderName;

    private Integer productPowerCd;

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

    public String getPriorityOrderName() {
        return priorityOrderName;
    }

    public void setPriorityOrderName(String priorityOrderName) {
        this.priorityOrderName = priorityOrderName == null ? null : priorityOrderName.trim();
    }

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    @Override
    public String toString() {
        return "PriorityOrderMst{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", priorityOrderName='" + priorityOrderName + '\'' +
                ", productPowerCd=" + productPowerCd +
                '}';
    }
}
