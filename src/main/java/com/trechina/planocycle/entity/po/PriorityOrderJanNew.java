package com.trechina.planocycle.entity.po;

import java.math.BigDecimal;

public class PriorityOrderJanNew {
    private String companyCd;

    private Integer priorityOrderCd;

    private String janNew;

    private Integer rank;

    private Integer branchNum;

    private BigDecimal branchAccount;

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

    public String getJanNew() {
        return janNew;
    }

    public void setJanNew(String janNew) {
        this.janNew = janNew;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getBranchNum() {
        return branchNum;
    }

    public void setBranchNum(Integer branchNum) {
        this.branchNum = branchNum;
    }

    public BigDecimal getBranchAccount() {
        return branchAccount;
    }

    public void setBranchAccount(BigDecimal branchAccount) {
        this.branchAccount = branchAccount;
    }

    @Override
    public String toString() {
        return "PriorityOrderJanNew{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", janNew='" + janNew + '\'' +
                ", rank=" + rank +
                ", branchNum=" + branchNum +
                ", branchAccount=" + branchAccount +
                '}';
    }
}
