package com.trechina.planocycle.entity.vo;

public class ClassicPriorityOrderJanReplaceVO {
    private String janOld;
    private String janNew;
    private String janName;

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

    @Override
    public String toString() {
        return "PriorityOrderJanReplaceVO{" +
                "janOld='" + janOld + '\'' +
                ", janNew='" + janNew + '\'' +
                ", janName='" + janName + '\'' +
                '}';
    }
}
