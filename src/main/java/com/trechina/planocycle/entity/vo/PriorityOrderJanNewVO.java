package com.trechina.planocycle.entity.vo;

import java.math.BigDecimal;

public class PriorityOrderJanNewVO {
    private String janNew;

    private String janName;

    private String attr;

    private Integer rank;

    private Integer branchNum;

    private BigDecimal branchAccount;

    private String errMsg;

    public String getJanNew() {
        return janNew;
    }

    public void setJanNew(String janNew) {
        this.janNew = janNew;
    }

    public String getJanName() {
        return janName;
    }

    public void setJanName(String janName) {
        this.janName = janName;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
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

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }


    @Override
    public String toString() {
        return "PriorityOrderJanNewVO{" +
                "janNew='" + janNew + '\'' +
                ", janName='" + janName + '\'' +
                ", attr='" + attr + '\'' +
                ", rank=" + rank +
                ", branchNum=" + branchNum +
                ", branchAccount=" + branchAccount +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
