package com.trechina.planocycle.entity.po;

public class PriorityOrderBranchNum {
    private String companyCd;

    private Integer priorityOrderCd;

    private Integer branch;

    private String janNew;

    private Integer branchNum;

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

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public String getJanNew() {
        return janNew;
    }

    public void setJanNew(String janNew) {
        this.janNew = janNew == null ? null : janNew.trim();
    }

    public Integer getBranchNum() {
        return branchNum;
    }

    public void setBranchNum(Integer branchNum) {
        this.branchNum = branchNum;
    }
}