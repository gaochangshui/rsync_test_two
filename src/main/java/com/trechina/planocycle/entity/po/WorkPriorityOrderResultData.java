package com.trechina.planocycle.entity.po;

public class WorkPriorityOrderResultData {
    private String companyCd;

    private String authorCd;

    private String janCd;

    private Long restrictCd;

    private Long skuRank;

    private Integer adoptFlag;

    private Long face;

    private Long faceSku;

    private Long irisu;

    private Integer taiCd;

    private Integer tanaCd;

    private Integer restrictType;

    private Long faceKeisan;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd == null ? null : authorCd.trim();
    }

    public String getJanCd() {
        return janCd;
    }

    public void setJanCd(String janCd) {
        this.janCd = janCd == null ? null : janCd.trim();
    }

    public Long getRestrictCd() {
        return restrictCd;
    }

    public void setRestrictCd(Long restrictCd) {
        this.restrictCd = restrictCd;
    }

    public Long getSkuRank() {
        return skuRank;
    }

    public void setSkuRank(Long skuRank) {
        this.skuRank = skuRank;
    }

    public Integer getAdoptFlag() {
        return adoptFlag;
    }

    public void setAdoptFlag(Integer adoptFlag) {
        this.adoptFlag = adoptFlag;
    }

    public Long getFace() {
        return face;
    }

    public void setFace(Long face) {
        this.face = face;
    }

    public Long getFaceSku() {
        return faceSku;
    }

    public void setFaceSku(Long faceSku) {
        this.faceSku = faceSku;
    }

    public Long getIrisu() {
        return irisu;
    }

    public void setIrisu(Long irisu) {
        this.irisu = irisu;
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

    public Integer getRestrictType() {
        return restrictType;
    }

    public void setRestrictType(Integer restrictType) {
        this.restrictType = restrictType;
    }

    public Long getFaceKeisan() {
        return faceKeisan;
    }

    public void setFaceKeisan(Long faceKeisan) {
        this.faceKeisan = faceKeisan;
    }
}