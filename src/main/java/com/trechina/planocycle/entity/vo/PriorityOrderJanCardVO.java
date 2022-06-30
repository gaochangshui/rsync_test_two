package com.trechina.planocycle.entity.vo;

public class PriorityOrderJanCardVO {
    private String janCd;
    private String janName;
    private String errMsg;

    public String getJanCd() {
        return janCd;
    }

    public void setJanCd(String janCd) {
        this.janCd = janCd;
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
        return "PriorityOrderJanCardVO{" +
                "janCd='" + janCd + '\'' +
                ", janName='" + janName + '\'' +
                '}';
    }
}
