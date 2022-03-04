package com.trechina.planocycle.entity.po;

import java.util.Date;

public class ShelfPtsDataHistory {
    private Integer ptsCd;

    private Integer patternCd;

    private String companyCd;

    private Integer validFlg;

    private Date startDay;

    private Date createTime;

    private String authorCd;

    private Date editTime;

    private String editerCd;

    private Integer deleteflg;

    public Integer getPtsCd() {
        return ptsCd;
    }

    public void setPtsCd(Integer ptsCd) {
        this.ptsCd = ptsCd;
    }

    public Integer getPatternCd() {
        return patternCd;
    }

    public void setPatternCd(Integer patternCd) {
        this.patternCd = patternCd;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public Integer getValidFlg() {
        return validFlg;
    }

    public void setValidFlg(Integer validFlg) {
        this.validFlg = validFlg;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
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