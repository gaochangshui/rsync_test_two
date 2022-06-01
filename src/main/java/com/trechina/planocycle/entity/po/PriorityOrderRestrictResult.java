package com.trechina.planocycle.entity.po;

public class PriorityOrderRestrictResult {
    private String companyCd;

    private Integer priorityOrderCd;

    private Long restrictCd;

    private Integer category;

    private Integer pkg;

    private Integer capacity;

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

    public Long getRestrictCd() {
        return restrictCd;
    }

    public void setRestrictCd(Long restrictCd) {
        this.restrictCd = restrictCd;
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
}