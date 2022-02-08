package com.trechina.planocycle.entity.vo;

public class PriorityOrderJanCardVO {
    private String janOld;
    private String janOldName;
    private String errMsg;

    public String getJanOld() {
        return janOld;
    }

    public void setJanOld(String janOld) {
        this.janOld = janOld;
    }

    public String getJanOldName() {
        return janOldName;
    }

    public void setJanOldName(String janOldName) {
        this.janOldName = janOldName;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "PriorityOrderJanCardVO{" +
                "janOld='" + janOld + '\'' +
                ", janOldName='" + janOldName + '\'' +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
