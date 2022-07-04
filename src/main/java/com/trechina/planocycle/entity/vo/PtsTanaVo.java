package com.trechina.planocycle.entity.vo;

import com.trechina.planocycle.entity.po.BasicPatternRestrictRelation;

import java.util.List;

public class PtsTanaVo {
    private Integer taiCd;
    private Integer tanaCd;
    private Integer tanaHeight;
    private Integer tanaWidth;
    private Integer tanaDepth;
    private Integer tanaThickness;
    private Integer tanaType;
    private String companyCd;
    private Integer priorityOrderCd;
    private List<BasicPatternRestrictRelation> group;
    private String remarks;

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

    public Integer getTanaHeight() {
        return tanaHeight;
    }

    public void setTanaHeight(Integer tanaHeight) {
        this.tanaHeight = tanaHeight;
    }

    public Integer getTanaWidth() {
        return tanaWidth;
    }

    public void setTanaWidth(Integer tanaWidth) {
        this.tanaWidth = tanaWidth;
    }

    public Integer getTanaDepth() {
        return tanaDepth;
    }

    public void setTanaDepth(Integer tanaDepth) {
        this.tanaDepth = tanaDepth;
    }

    public Integer getTanaThickness() {
        return tanaThickness;
    }

    public void setTanaThickness(Integer tanaThickness) {
        this.tanaThickness = tanaThickness;
    }

    public Integer getTanaType() {
        return tanaType;
    }

    public void setTanaType(Integer tanaType) {
        this.tanaType = tanaType;
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

    public List<BasicPatternRestrictRelation> getGroup() {
        return group;
    }

    public void setGroup(List<BasicPatternRestrictRelation> group) {
        this.group = group;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "PtsTanaVo{" +
                "taiCd=" + taiCd +
                ", tanaCd=" + tanaCd +
                ", tanaHeight=" + tanaHeight +
                ", tanaWidth=" + tanaWidth +
                ", tanaDepth=" + tanaDepth +
                ", tanaThickness=" + tanaThickness +
                ", tanaType=" + tanaType +
                ", companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", group=" + group +
                ", remarks=" + remarks +
                '}';
    }
}
