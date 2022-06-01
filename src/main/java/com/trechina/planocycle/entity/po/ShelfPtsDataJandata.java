package com.trechina.planocycle.entity.po;

import java.util.Date;

public class ShelfPtsDataJandata {
    private String companyCd;

    private Integer ptsCd;

    private Integer taiCd;

    private Integer tanaCd;

    private Integer tanapositionCd;

    private String jan;

    private Integer faceCount;

    private Integer faceMen;

    private Integer faceKaiten;

    private Integer tumiagesu;

    private Integer zaikosu;

    private Integer faceDisplayflg;

    private Integer facePosition;

    private Integer depthDisplayNum;

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

    public Integer getTanapositionCd() {
        return tanapositionCd;
    }

    public void setTanapositionCd(Integer tanapositionCd) {
        this.tanapositionCd = tanapositionCd;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public Integer getFaceCount() {
        return faceCount;
    }

    public void setFaceCount(Integer faceCount) {
        this.faceCount = faceCount;
    }

    public Integer getFaceMen() {
        return faceMen;
    }

    public void setFaceMen(Integer faceMen) {
        this.faceMen = faceMen;
    }

    public Integer getFaceKaiten() {
        return faceKaiten;
    }

    public void setFaceKaiten(Integer faceKaiten) {
        this.faceKaiten = faceKaiten;
    }

    public Integer getTumiagesu() {
        return tumiagesu;
    }

    public void setTumiagesu(Integer tumiagesu) {
        this.tumiagesu = tumiagesu;
    }

    public Integer getZaikosu() {
        return zaikosu;
    }

    public void setZaikosu(Integer zaikosu) {
        this.zaikosu = zaikosu;
    }

    public Integer getFaceDisplayflg() {
        return faceDisplayflg;
    }

    public void setFaceDisplayflg(Integer faceDisplayflg) {
        this.faceDisplayflg = faceDisplayflg;
    }

    public Integer getFacePosition() {
        return facePosition;
    }

    public void setFacePosition(Integer facePosition) {
        this.facePosition = facePosition;
    }

    public Integer getDepthDisplayNum() {
        return depthDisplayNum;
    }

    public void setDepthDisplayNum(Integer depthDisplayNum) {
        this.depthDisplayNum = depthDisplayNum;
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