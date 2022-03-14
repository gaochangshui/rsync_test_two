package com.trechina.planocycle.entity.dto;

public class PriorityOrderRestDto {
    private String restrict1;
    private String restrict2;
    private String restrict3;
    private String restrict4;
    private String restrictName1;
    private String restrictName2;
    private String restrictName3;
    private String restrictName4;
    private Integer skuNum;
    private Integer faceNum;
    public String getRestrict1() {
        return restrict1;
    }

    public void setRestrict1(String restrict1) {
        this.restrict1 = restrict1;
    }

    public String getRestrict2() {
        return restrict2;
    }

    public void setRestrict2(String restrict2) {
        this.restrict2 = restrict2;
    }

    public String getRestrict3() {
        return restrict3;
    }

    public void setRestrict3(String restrict3) {
        this.restrict3 = restrict3;
    }

    public String getRestrict4() {
        return restrict4;
    }

    public void setRestrict4(String restrict4) {
        this.restrict4 = restrict4;
    }

    public String getRestrictName1() {
        return restrictName1;
    }

    public void setRestrictName1(String restrictName1) {
        this.restrictName1 = restrictName1;
    }

    public String getRestrictName2() {
        return restrictName2;
    }

    public void setRestrictName2(String restrictName2) {
        this.restrictName2 = restrictName2;
    }

    public String getRestrictName3() {
        return restrictName3;
    }

    public void setRestrictName3(String restrictName3) {
        this.restrictName3 = restrictName3;
    }

    public String getRestrictName4() {
        return restrictName4;
    }

    public void setRestrictName4(String restrictName4) {
        this.restrictName4 = restrictName4;
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
                "restrict1='" + restrict1 + '\'' +
                ", restrict2='" + restrict2 + '\'' +
                ", restrict3='" + restrict3 + '\'' +
                ", restrict4='" + restrict4 + '\'' +
                ", restrictName1='" + restrictName1 + '\'' +
                ", restrictName2='" + restrictName2 + '\'' +
                ", restrictName3='" + restrictName3 + '\'' +
                ", restrictName4='" + restrictName4 + '\'' +
                ", skuNum=" + skuNum +
                ", faceNum=" + faceNum +
                '}';
    }
}
