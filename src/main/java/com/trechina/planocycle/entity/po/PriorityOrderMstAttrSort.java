package com.trechina.planocycle.entity.po;

public class PriorityOrderMstAttrSort {
    private String companyCd;

    private Integer priorityOrderCd;

    private Integer value;

    private Integer sort;

    private Integer cd;

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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getCd() {
        return cd;
    }

    public void setCd(Integer cd) {
        this.cd = cd;
    }

    @Override
    public String toString() {
        return "PriorityOrderMstAttrSort{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", value=" + value +
                ", sort=" + sort +
                ", cd=" + cd +
                '}';
    }
}
