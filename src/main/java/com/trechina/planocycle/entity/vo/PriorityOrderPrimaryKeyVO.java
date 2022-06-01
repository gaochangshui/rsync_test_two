package com.trechina.planocycle.entity.vo;

public class PriorityOrderPrimaryKeyVO {
    private String companyCd;

    private Integer priorityOrderCd;

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

    @Override
    public String toString() {
        return "PriorityOrderPrimaryKeyVO{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                '}';
    }
}
