package com.trechina.planocycle.entity.po;

public class PriorityAllMst {

    private String companyCd;

    private Integer priorityAllCd;

    private String priorityAllName;

    private Integer productPowerCd;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public Integer getPriorityAllCd() {
        return priorityAllCd;
    }

    public void setPriorityAllCd(Integer priorityAllCd) {
        this.priorityAllCd = priorityAllCd;
    }

    public String getPriorityAllName() {
        return priorityAllName;
    }

    public void setPriorityAllName(String priorityAllName) {
        this.priorityAllName = priorityAllName == null ? null : priorityAllName.trim();
    }

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

}
