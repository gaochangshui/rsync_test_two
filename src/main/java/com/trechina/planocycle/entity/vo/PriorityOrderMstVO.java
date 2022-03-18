package com.trechina.planocycle.entity.vo;

public class PriorityOrderMstVO {
    private String priorityOrderName;
    private Integer priorityOrderCd;
    private String companyCd;

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getPriorityOrderName() {
        return priorityOrderName;
    }

    public void setPriorityOrderName(String priorityOrderName) {
        this.priorityOrderName = priorityOrderName;
    }
}
