package com.trechina.planocycle.entity.po;

public class PriorityOrderCommodityMust {
    private String companyCd;

    private Integer priorityOrderCd;

    private String branch;

    private String jan;

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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    @Override
    public String toString() {
        return "PriorityOrderCommodityMust{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", branch='" + branch + '\'' +
                ", jan='" + jan + '\'' +
                '}';
    }
}
