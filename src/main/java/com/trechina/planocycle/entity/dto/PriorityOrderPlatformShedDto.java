package com.trechina.planocycle.entity.dto;

public class PriorityOrderPlatformShedDto {
    private Integer taiCd;
    private Integer tanaCd;
    private Integer restrictType;
    private Integer faceNum;
    private Integer skuNum;

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

    public Integer getFaceNum() {
        return faceNum;
    }

    public void setFaceNum(Integer faceNum) {
        this.faceNum = faceNum;
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum;
    }

    @Override
    public String toString() {
        return "PriorityOrderPlatformShedDto{" +
                "taiCd=" + taiCd +
                ", tanaCd=" + tanaCd +
                ", restrictType=" + restrictType +
                ", faceNum=" + faceNum +
                ", skuNum=" + skuNum +
                '}';
    }
}
