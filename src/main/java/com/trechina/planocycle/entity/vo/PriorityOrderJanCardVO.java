package com.trechina.planocycle.entity.vo;

public class PriorityOrderJanCardVO {
    private String janCd;
    private String janName;

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

    @Override
    public String toString() {
        return "PriorityOrderJanCardVO{" +
                "janCd='" + janCd + '\'' +
                ", janName='" + janName + '\'' +
                '}';
    }
}
