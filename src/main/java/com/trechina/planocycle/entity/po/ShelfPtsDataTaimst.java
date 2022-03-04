package com.trechina.planocycle.entity.po;

import java.util.Date;

public class ShelfPtsDataTaimst {
    private String companyCd;

    private Integer ptsCd;

    private Integer taiCd;

    private Integer taiHeight;

    private Integer taiWidth;

    private Integer taiDepth;

    private String taiName;

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

    public Integer getTaiHeight() {
        return taiHeight;
    }

    public void setTaiHeight(Integer taiHeight) {
        this.taiHeight = taiHeight;
    }

    public Integer getTaiWidth() {
        return taiWidth;
    }

    public void setTaiWidth(Integer taiWidth) {
        this.taiWidth = taiWidth;
    }

    public Integer getTaiDepth() {
        return taiDepth;
    }

    public void setTaiDepth(Integer taiDepth) {
        this.taiDepth = taiDepth;
    }

    public String getTaiName() {
        return taiName;
    }

    public void setTaiName(String taiName) {
        this.taiName = taiName == null ? null : taiName.trim();
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