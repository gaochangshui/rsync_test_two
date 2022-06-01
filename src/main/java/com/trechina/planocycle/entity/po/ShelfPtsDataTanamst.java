package com.trechina.planocycle.entity.po;

import java.util.Date;

public class ShelfPtsDataTanamst {
    private String companyCd;

    private Integer ptsCd;

    private Integer taiCd;

    private Integer tanaCd;

    private Integer tanaHeight;

    private Integer tanaWidth;

    private Integer tanaDepth;

    private Integer tanaThickness;

    private Integer tanaType;

    private Date createTime;

    private String authorCd;

    private Date editTime;

    private String editerCd;

    private Integer deleteflg;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public Integer getPtsCd() {
        return ptsCd;
    }

    public void setPtsCd(Integer ptsCd) {
        this.ptsCd = ptsCd;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd == null ? null : authorCd.trim();
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public String getEditerCd() {
        return editerCd;
    }

    public void setEditerCd(String editerCd) {
        this.editerCd = editerCd == null ? null : editerCd.trim();
    }

    public Integer getDeleteflg() {
        return deleteflg;
    }

    public void setDeleteflg(Integer deleteflg) {
        this.deleteflg = deleteflg;
    }
}