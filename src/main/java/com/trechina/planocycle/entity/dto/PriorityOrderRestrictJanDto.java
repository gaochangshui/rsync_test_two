package com.trechina.planocycle.entity.dto;



public class PriorityOrderRestrictJanDto {
    private Integer taiCd;
    private  Integer tanaCd;
    private Integer restrictType;
    private String janCd;
    private  String janName;
    private Integer rank;
    private Integer faceNum;
    private Integer irisu;
    private String scat1NameVal;
    private String scat2NameVal;
    private String scat3NameVal;
    private String scat4NameVal;
    private String pkg;
    private String kikaku;
    private String alcohol;
    private String brand;

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

    public String getJanCd() {
        return janCd;
    }

    public void setJanCd(String janCd) {
        this.janCd = janCd;
    }

    public String getJanName() {
        return janName;
    }

    public void setJanName(String janName) {
        this.janName = janName;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getFaceNum() {
        return faceNum;
    }

    public void setFaceNum(Integer faceNum) {
        this.faceNum = faceNum;
    }

    public Integer getIrisu() {
        return irisu;
    }

    public void setIrisu(Integer irisu) {
        this.irisu = irisu;
    }

    public String getScat1NameVal() {
        return scat1NameVal;
    }

    public void setScat1NameVal(String scat1NameVal) {
        this.scat1NameVal = scat1NameVal;
    }

    public String getScat2NameVal() {
        return scat2NameVal;
    }

    public void setScat2NameVal(String scat2NameVal) {
        this.scat2NameVal = scat2NameVal;
    }

    public String getScat3NameVal() {
        return scat3NameVal;
    }

    public void setScat3NameVal(String scat3NameVal) {
        this.scat3NameVal = scat3NameVal;
    }

    public String getScat4NameVal() {
        return scat4NameVal;
    }

    public void setScat4NameVal(String scat4NameVal) {
        this.scat4NameVal = scat4NameVal;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getKikaku() {
        return kikaku;
    }

    public void setKikaku(String kikaku) {
        this.kikaku = kikaku;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "PriorityOrderRestrictJanDto{" +
                "taiCd=" + taiCd +
                ", tanaCd=" + tanaCd +
                ", restrictType=" + restrictType +
                ", janCd=" + janCd +
                ", janName='" + janName + '\'' +
                ", rank=" + rank +
                ", faceNum=" + faceNum +
                ", irisu=" + irisu +
                ", scat1NameVal='" + scat1NameVal + '\'' +
                ", scat2NameVal='" + scat2NameVal + '\'' +
                ", scat3NameVal='" + scat3NameVal + '\'' +
                ", scat4NameVal='" + scat4NameVal + '\'' +
                ", pkg='" + pkg + '\'' +
                ", kikaku='" + kikaku + '\'' +
                ", alcohol='" + alcohol + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}
