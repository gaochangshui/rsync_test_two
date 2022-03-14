package com.trechina.planocycle.entity.dto;

public class PriorityOrderAttrValueDto {
    private Integer zokuseiId;
    private String val;
    private String nm;

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

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    @Override
    public String toString() {
        return "PriorityOrderAttrValueDto{" +
                "zokuseiId=" + zokuseiId +
                ", val='" + val + '\'' +
                ", nm='" + nm + '\'' +
                '}';
    }
}
