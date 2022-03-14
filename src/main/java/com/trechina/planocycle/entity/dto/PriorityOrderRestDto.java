package com.trechina.planocycle.entity.dto;

public class PriorityOrderRestDto {
    private Integer restrictCd;
    private String restrictName;

    private Integer skuNum;
    private Integer faceNum;

    public Integer getRestrictCd() {
        return restrictCd;
    }

    public void setRestrictCd(Integer restrictCd) {
        this.restrictCd = restrictCd;
    }

    public String getRestrictName() {
        return restrictName;
    }

    public void setRestrictName(String restrictName) {
        this.restrictName = restrictName;
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum;
    }

    public Integer getFaceNum() {
        return faceNum;
    }

    public void setFaceNum(Integer faceNum) {
        this.faceNum = faceNum;
    }

    @Override
    public String toString() {
        return "PriorityOrderRestDto{" +
                "restrictCd='" + restrictCd + '\'' +
                ", restrictName='" + restrictName + '\'' +
                ", skuNum=" + skuNum +
                ", faceNum=" + faceNum +
                '}';
    }
}
