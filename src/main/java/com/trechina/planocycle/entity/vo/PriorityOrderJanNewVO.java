package com.trechina.planocycle.entity.vo;

public class PriorityOrderJanNewVO {

    private String companyCd;
    private Integer priorityOrderCd;
    private String janNew;

    private String janName;

    private Integer rank;

    private String scat1cdVal;

    private String scat2cdVal;

    private String scat3cdVal;

    private String scat4cdVal;

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

    public String getScat1cdVal() {
        return scat1cdVal;
    }

    public void setScat1cdVal(String scat1cdVal) {
        this.scat1cdVal = scat1cdVal;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getScat2cdVal() {
        return scat2cdVal;
    }

    public void setScat2cdVal(String scat2cdVal) {
        this.scat2cdVal = scat2cdVal;
    }

    public String getScat3cdVal() {
        return scat3cdVal;
    }

    public void setScat3cdVal(String scat3cdVal) {
        this.scat3cdVal = scat3cdVal;
    }

    public String getScat4cdVal() {
        return scat4cdVal;
    }

    public void setScat4cdVal(String scat4cdVal) {
        this.scat4cdVal = scat4cdVal;
    }

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

    @Override
    public String toString() {
        return "PriorityOrderJanNewVO{" +
                "janNew='" + janNew + '\'' +
                ", janName='" + janName + '\'' +
                ", rank=" + rank +
                ", scat1cdVal='" + scat1cdVal + '\'' +
                ", scat2cdVal='" + scat2cdVal + '\'' +
                ", scat3cdVal='" + scat3cdVal + '\'' +
                ", scat4cdVal='" + scat4cdVal + '\'' +
                '}';
    }
}
