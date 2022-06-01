package com.trechina.planocycle.entity.dto;

public class PriorityOrderPlatformShedDto {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer taiCd;
    private Integer tanaCd;
    private Integer tanaType;
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

    public Integer getTanaType() {
        return tanaType;
    }

    public void setTanaType(Integer tanaType) {
        this.tanaType = tanaType;
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

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    @Override
    public String toString() {
        return "PriorityOrderPlatformShedDto{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", taiCd=" + taiCd +
                ", tanaCd=" + tanaCd +
                ", tanaType=" + tanaType +
                ", faceNum=" + faceNum +
                ", skuNum=" + skuNum +
                '}';
    }
}
