package com.trechina.planocycle.entity.po;

import java.math.BigDecimal;

public class ClassicPriorityOrderJanNew {
    private String companyCd;

    private Integer priorityOrderCd;

    private String janNew;

    private Integer rank;

    private Integer branchNum;

    private BigDecimal branchAccount;

    private String nameNew;

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

    public String getJanNew() {
        return janNew;
    }

    public void setJanNew(String janNew) {
        this.janNew = janNew == null ? null : janNew.trim();
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getBranchnum() {
        return branchNum;
    }

    public void setBranchnum(Integer branchnum) {
        this.branchNum = branchnum;
    }

    public BigDecimal getBranchAccount() {
        return branchAccount;
    }

    public void setBranchAccount(BigDecimal branchAccount) {
        this.branchAccount = branchAccount;
    }

    public String getNameNew() {
        return nameNew;
    }

    public void setNameNew(String nameNew) {
        this.nameNew = nameNew;
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
                ", nameNew=" + nameNew +
                '}';
    }
}
