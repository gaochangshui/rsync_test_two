package com.trechina.planocycle.entity.vo;

public class PriorityOrderJanProposalVO {
    private String janOld;

    private String janNew;

    private String janName;

    private Integer flag;

    private String errMsg;

    public String getJanOld() {
        return janOld;
    }

    public void setJanOld(String janOld) {
        this.janOld = janOld;
    }

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

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "PriorityOrderJanProposalVO{" +
                "janOld='" + janOld + '\'' +
                ", janNew='" + janNew + '\'' +
                ", janName='" + janName + '\'' +
                ", flag=" + flag +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
