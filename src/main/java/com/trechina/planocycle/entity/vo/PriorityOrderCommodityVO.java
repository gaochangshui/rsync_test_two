package com.trechina.planocycle.entity.vo;

public class PriorityOrderCommodityVO {
    private Integer branch;
    private String branchName;
    private String jan;
    private String janName;
    private String errMsg;

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getJanName() {
        return janName;
    }

    public void setJanName(String janName) {
        this.janName = janName;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "PriorityOrderCommodityVO{" +
                "branch=" + branch +
                ", branchName='" + branchName + '\'' +
                ", jan='" + jan + '\'' +
                ", janName='" + janName + '\'' +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
