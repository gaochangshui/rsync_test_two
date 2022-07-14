package com.trechina.planocycle.entity.vo;

public class PtsJanDataVo {

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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "PtsJanDataVo{" +
                "taiCd=" + taiCd +
                ", tanaCd=" + tanaCd +
                ", tanapositionCd=" + tanapositionCd +
                ", jan='" + jan + '\'' +
                ", faceCount=" + faceCount +
                ", faceMen=" + faceMen +
                ", faceKaiten=" + faceKaiten +
                ", tumiagesu=" + tumiagesu +
                ", zaikosu=" + zaikosu +
                ", faceDisplayflg=" + faceDisplayflg +
                ", facePosition=" + facePosition +
                ", depthDisplayNum=" + depthDisplayNum +
                ", remarks=" + remarks +
                '}';
    }
}
