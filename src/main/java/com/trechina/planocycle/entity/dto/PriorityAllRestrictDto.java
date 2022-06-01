package com.trechina.planocycle.entity.dto;

import java.math.BigDecimal;

public class PriorityAllRestrictDto {
    private String companyCd;
    private Integer priorityAllCd;
    private String authorCd;
    private Integer patternCd;
    private String zokusei1;
    private String zokusei2;
    private String zokusei3;
    private String zokusei4;
    private String zokusei5;
    private String zokusei6;
    private String zokusei7;
    private String zokusei8;
    private String zokusei9;
    private String zokusei10;
    private Long restrictCd;
    private BigDecimal tanaCnt;
    private BigDecimal basicTanaCnt;
    private Integer skuCnt;

    public BigDecimal getBasicTanaCnt() {
        return basicTanaCnt;
    }

    public void setBasicTanaCnt(BigDecimal basicTanaCnt) {
        this.basicTanaCnt = basicTanaCnt;
    }

    public String getZokusei1() {
        return zokusei1;
    }

    public void setZokusei1(String zokusei1) {
        this.zokusei1 = zokusei1;
    }

    public String getZokusei2() {
        return zokusei2;
    }

    public void setZokusei2(String zokusei2) {
        this.zokusei2 = zokusei2;
    }

    public String getZokusei3() {
        return zokusei3;
    }

    public void setZokusei3(String zokusei3) {
        this.zokusei3 = zokusei3;
    }

    public String getZokusei4() {
        return zokusei4;
    }

    public void setZokusei4(String zokusei4) {
        this.zokusei4 = zokusei4;
    }

    public String getZokusei5() {
        return zokusei5;
    }

    public void setZokusei5(String zokusei5) {
        this.zokusei5 = zokusei5;
    }

    public String getZokusei6() {
        return zokusei6;
    }

    public void setZokusei6(String zokusei6) {
        this.zokusei6 = zokusei6;
    }

    public String getZokusei7() {
        return zokusei7;
    }

    public void setZokusei7(String zokusei7) {
        this.zokusei7 = zokusei7;
    }

    public String getZokusei8() {
        return zokusei8;
    }

    public void setZokusei8(String zokusei8) {
        this.zokusei8 = zokusei8;
    }

    public String getZokusei9() {
        return zokusei9;
    }

    public void setZokusei9(String zokusei9) {
        this.zokusei9 = zokusei9;
    }

    public String getZokusei10() {
        return zokusei10;
    }

    public void setZokusei10(String zokusei10) {
        this.zokusei10 = zokusei10;
    }

    public Long getRestrictCd() {
        return restrictCd;
    }

    public void setRestrictCd(Long restrictCd) {
        this.restrictCd = restrictCd;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getPriorityAllCd() {
        return priorityAllCd;
    }

    public void setPriorityAllCd(Integer priorityAllCd) {
        this.priorityAllCd = priorityAllCd;
    }

    public Integer getPatternCd() {
        return patternCd;
    }

    public void setPatternCd(Integer patternCd) {
        this.patternCd = patternCd;
    }

    public BigDecimal getTanaCnt() {
        return tanaCnt;
    }

    public void setTanaCnt(BigDecimal tanaCnt) {
        this.tanaCnt = tanaCnt;
    }

    public Integer getSkuCnt() {
        return skuCnt;
    }

    public void setSkuCnt(Integer skuCnt) {
        this.skuCnt = skuCnt;
    }

    @Override
    public String toString() {
        return "PriorityAllRestrictDto{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityAllCd=" + priorityAllCd +
                ", patternCd=" + patternCd +
                ", zokusei1='" + zokusei1 + '\'' +
                ", zokusei2='" + zokusei2 + '\'' +
                ", zokusei3='" + zokusei3 + '\'' +
                ", zokusei4='" + zokusei4 + '\'' +
                ", zokusei5='" + zokusei5 + '\'' +
                ", zokusei6='" + zokusei6 + '\'' +
                ", zokusei7='" + zokusei7 + '\'' +
                ", zokusei8='" + zokusei8 + '\'' +
                ", zokusei9='" + zokusei9 + '\'' +
                ", zokusei10='" + zokusei10 + '\'' +
                ", restrictCd=" + restrictCd +
                ", tanaCnt=" + tanaCnt +
                ", skuCnt=" + skuCnt +
                '}';
    }


    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd;
    }
}
