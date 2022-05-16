package com.trechina.planocycle.entity.po;

public class PriorityOrderAttributeClassify {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer taiCd;
    private String attr1;
    private Integer tanaCd;
    private String attr2;
    private boolean taiCdFlg = false;
    private boolean tanaCdFlg = false;

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

    public Integer getTaiCd() {
        return taiCd;
    }

    public void setTaiCd(Integer taiCd) {
        this.taiCd = taiCd;
    }

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public Integer getTanaCd() {
        return tanaCd;
    }

    public void setTanaCd(Integer tanaCd) {
        this.tanaCd = tanaCd;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }


    public boolean isTaiCdFlg() {
        return taiCdFlg;
    }

    public void setTaiCdFlg(boolean taiCdFlg) {
        this.taiCdFlg = taiCdFlg;
    }

    public boolean isTanaCdFlg() {
        return tanaCdFlg;
    }

    public void setTanaCdFlg(boolean tanaCdFlg) {
        this.tanaCdFlg = tanaCdFlg;
    }

    @Override
    public String toString() {
        return "PriorityOrderAttributeClassify{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", taiCd=" + taiCd +
                ", attr1='" + attr1 + '\'' +
                ", tanaCd=" + tanaCd +
                ", attr2='" + attr2 + '\'' +
                ", taiCdFlg=" + taiCdFlg +
                ", tanaCdFlg=" + tanaCdFlg +
                '}';
    }
}
