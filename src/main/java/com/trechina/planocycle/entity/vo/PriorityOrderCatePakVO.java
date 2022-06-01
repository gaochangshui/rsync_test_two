package com.trechina.planocycle.entity.vo;

public class PriorityOrderCatePakVO {

    private Integer id;
    private String smalls;
    private Integer rank;
    private Integer branchNum;
    private String bigs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSmalls() {
        return smalls;
    }

    public void setSmalls(String smalls) {
        this.smalls = smalls;
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

    public String getBigs() {
        return bigs;
    }

    public void setBigs(String bigs) {
        this.bigs = bigs;
    }

    @Override
    public String toString() {
        return "PriorityOrderCatePakVO{" +
                "id=" + id +
                ", smalls='" + smalls + '\'' +
                ", rank=" + rank +
                ", branchNum=" + branchNum +
                ", bigs='" + bigs + '\'' +
                '}';
    }
}
