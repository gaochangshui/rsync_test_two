package com.trechina.planocycle.entity.po;


public class PriorityOrderRestrictSet {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer taiCd;
    private Integer tanaCd;
    private Integer restrictType;
    private Integer category;
    private Integer pkg;
    private Integer capacity;

    public PriorityOrderRestrictSet(String companyCd, Integer priorityOrderCd, Integer taiCd, Integer tanaCd, Integer restrictType, Integer category, Integer pkg, Integer capacity) {
        this.companyCd = companyCd;
        this.priorityOrderCd = priorityOrderCd;
        this.taiCd = taiCd;
        this.tanaCd = tanaCd;
        this.restrictType = restrictType;
        this.category = category;
        this.pkg = pkg;
        this.capacity = capacity;
    }

    public PriorityOrderRestrictSet() {
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public Integer getTaiCd() {
        return taiCd;
    }

    public void setTaiCd(Integer taiCd) {
        this.taiCd = taiCd;
    }

    public Integer getTanaCd() {
        return tanaCd;
    }

    public void setTanaCd(Integer tanaCd) {
        this.tanaCd = tanaCd;
    }

    public Integer getRestrictType() {
        return restrictType;
    }

    public void setRestrictType(Integer restrictType) {
        this.restrictType = restrictType;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getPkg() {
        return pkg;
    }

    public void setPkg(Integer pkg) {
        this.pkg = pkg;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "PriorityOrderRestrictSet{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", taiCd=" + taiCd +
                ", tanaCd=" + tanaCd +
                ", restrictType=" + restrictType +
                ", category=" + category +
                ", pkg=" + pkg +
                ", capacity=" + capacity +
                '}';
    }
}
