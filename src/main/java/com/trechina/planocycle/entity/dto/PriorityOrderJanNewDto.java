package com.trechina.planocycle.entity.dto;

public class PriorityOrderJanNewDto {

    private String companyCd;
    private Integer priorityOrderCd;
    private String janNew;

    private String janName;

    private Integer rank;

    private String zokusei1;

    private String zokusei2;

    private String zokusei3;

    private String zokusei4;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
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
        this.janNew = janNew;
    }

    public String getJanName() {
        return janName;
    }

    public void setJanName(String janName) {
        this.janName = janName;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getZokusei1() {
        return zokusei1;
    }

    public void setZokusei1(String zokusei1) {
        this.zokusei1 = zokusei1;
    }

    public String getZokusei2() {
        return zokusei2;
    }

    public void setZokusei2(String zokusei2) {
        this.zokusei2 = zokusei2;
    }

    public String getZokusei3() {
        return zokusei3;
    }

    public void setZokusei3(String zokusei3) {
        this.zokusei3 = zokusei3;
    }

    public String getZokusei4() {
        return zokusei4;
    }

    public void setZokusei4(String zokusei4) {
        this.zokusei4 = zokusei4;
    }

    @Override
    public String toString() {
        return "PriorityOrderJanNewDto{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", janNew='" + janNew + '\'' +
                ", janName='" + janName + '\'' +
                ", rank=" + rank +
                ", zokusei1='" + zokusei1 + '\'' +
                ", zokusei2='" + zokusei2 + '\'' +
                ", zokusei3='" + zokusei3 + '\'' +
                ", zokusei4='" + zokusei4 + '\'' +
                '}';
    }
}
