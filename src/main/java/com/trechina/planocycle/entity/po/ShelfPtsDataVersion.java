package com.trechina.planocycle.entity.po;

import java.util.Date;

public class ShelfPtsDataVersion {
    private String companyCd;

    private Integer ptsCd;

    private String commoninfo;

    private String versioninfo;

    private String outflg;

    private String modename;

    private String taiHeader;

    private String tanaHeader;

    private String janHeader;

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

    public String getCommoninfo() {
        return commoninfo;
    }

    public void setCommoninfo(String commoninfo) {
        this.commoninfo = commoninfo == null ? null : commoninfo.trim();
    }

    public String getVersioninfo() {
        return versioninfo;
    }

    public void setVersioninfo(String versioninfo) {
        this.versioninfo = versioninfo == null ? null : versioninfo.trim();
    }

    public String getOutflg() {
        return outflg;
    }

    public void setOutflg(String outflg) {
        this.outflg = outflg == null ? null : outflg.trim();
    }

    public String getModename() {
        return modename;
    }

    public void setModename(String modename) {
        this.modename = modename == null ? null : modename.trim();
    }

    public String getTaiHeader() {
        return taiHeader;
    }

    public void setTaiHeader(String taiHeader) {
        this.taiHeader = taiHeader == null ? null : taiHeader.trim();
    }

    public String getTanaHeader() {
        return tanaHeader;
    }

    public void setTanaHeader(String tanaHeader) {
        this.tanaHeader = tanaHeader == null ? null : tanaHeader.trim();
    }

    public String getJanHeader() {
        return janHeader;
    }

    public void setJanHeader(String janHeader) {
        this.janHeader = janHeader == null ? null : janHeader.trim();
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