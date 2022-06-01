package com.trechina.planocycle.entity.po;

public class ClassicPriorityOrderJanCard {
    private String companyCd;

    private Integer priorityOrderCd;

    private String janOld;

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

    public String getJanOld() {
        return janOld;
    }

    public void setJanOld(String janOld) {
        this.janOld = janOld == null ? null : janOld.trim();
    }
}