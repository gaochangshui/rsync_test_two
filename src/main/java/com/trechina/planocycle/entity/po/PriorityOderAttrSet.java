package com.trechina.planocycle.entity.po;

public class PriorityOderAttrSet {
    private String companyCd;
    private Integer taiCd;
    private Integer tanaCd;
    private Integer tanaType;
    private Integer zokuseiId;
    private String val;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getTaiCd() {
        return taiCd;
    }

    public void setTaiCd(Integer taiCd) {
        this.taiCd = taiCd;
    }

    public Integer getTanaCd() {
        return tanaCd;
    }

    public void setTanaCd(Integer tanaCd) {
        this.tanaCd = tanaCd;
    }

    public Integer gettanaType() {
        return tanaType;
    }

    public void settanaType(Integer tanaType) {
        this.tanaType = tanaType;
    }

    public Integer getZokuseiId() {
        return zokuseiId;
    }

    public void setZokuseiId(Integer zokuseiId) {
        this.zokuseiId = zokuseiId;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "PriorityOderAttrSet{" +
                "companyCd='" + companyCd + '\'' +
                ", taiCd=" + taiCd +
                ", tanaCd=" + tanaCd +
                ", tanaType=" + tanaType +
                ", zokuseiId=" + zokuseiId +
                ", val=" + val +
                '}';
    }
}
