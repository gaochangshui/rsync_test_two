package com.trechina.planocycle.entity.vo;

public class PriorityOrderJanReplaceVO {
    private String janCd;
    private String janNew;
    private String janName;
    private String janNewName;
    public String getJanCd() {
        return janCd;
    }

    public void setJanCd(String janCd) {
        this.janCd = janCd;
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

    @Override
    public String toString() {
        return "PriorityOrderJanReplaceVO{" +
                "janOld='" + janCd + '\'' +
                ", janNew='" + janNew + '\'' +
                ", janName='" + janName + '\'' +
                '}';
    }
}
