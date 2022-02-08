package com.trechina.planocycle.entity.po;

public class PriorityOrderJanProposal {
    private String companyCd;

    private Integer priorityOrderCd;

    private String janOld;

    private String janNew;

    private Integer flag;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public String getJanOld() {
        return janOld;
    }

    public void setJanOld(String janOld) {
        this.janOld = janOld == null ? null : janOld.trim();
    }

    public String getJanNew() {
        return janNew;
    }

    public void setJanNew(String janNew) {
        this.janNew = janNew == null ? null : janNew.trim();
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "PriorityOrderJanProposal{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", janOld='" + janOld + '\'' +
                ", janNew='" + janNew + '\'' +
                ", flag=" + flag +
                '}';
    }
}
