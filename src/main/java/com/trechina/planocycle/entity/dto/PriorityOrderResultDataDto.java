package com.trechina.planocycle.entity.dto;

public class PriorityOrderResultDataDto {
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

    private Integer tanaType;

    private Integer restrictType;

    private Long faceKeisan;
    private Double salesCnt;

    private Long janWidth;
    private Long janHeight;

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

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd;
    }

    public String getJanCd() {
        return janCd;
    }

    public void setJanCd(String janCd) {
        this.janCd = janCd;
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

    public Double getSalesCnt() {
        return salesCnt;
    }

    public void setSalesCnt(Double salesCnt) {
        this.salesCnt = salesCnt;
    }

    public Long getJanWidth() {
        return janWidth;
    }

    public void setJanWidth(Long janWidth) {
        this.janWidth = janWidth;
    }

    public Long getJanHeight() {
        return janHeight;
    }

    public void setJanHeight(Long janHeight) {
        this.janHeight = janHeight;
    }
}
