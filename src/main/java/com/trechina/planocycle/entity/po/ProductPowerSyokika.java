package com.trechina.planocycle.entity.po;

import java.math.BigDecimal;

/**
 * 商品力初期化数据
 * POS数据
 */
public class ProductPowerSyokika {
    private String companyCd;
    private Long productPowerCd;
    private String jan;
    private String skuName;
    private String classifyBig;
    private String classifyMiddle;
    private String classifySmall;
    private String classifyFine;
    private BigDecimal posAmount;
    private BigDecimal posNum;
    private BigDecimal branchAmount;
    private BigDecimal branchNum;
    private BigDecimal compareAmount;
    private BigDecimal compareNum;
    private BigDecimal branchCompareAmount;
    private BigDecimal branchCompareNum;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Long getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Long productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getClassifyBig() {
        return classifyBig;
    }

    public void setClassifyBig(String classifyBig) {
        this.classifyBig = classifyBig;
    }

    public String getClassifyMiddle() {
        return classifyMiddle;
    }

    public void setClassifyMiddle(String classifyMiddle) {
        this.classifyMiddle = classifyMiddle;
    }

    public String getClassifySmall() {
        return classifySmall;
    }

    public void setClassifySmall(String classifySmall) {
        this.classifySmall = classifySmall;
    }

    public String getClassifyFine() {
        return classifyFine;
    }

    public void setClassifyFine(String classifyFine) {
        this.classifyFine = classifyFine;
    }

    public BigDecimal getPosAmount() {
        return posAmount;
    }

    public void setPosAmount(BigDecimal posAmount) {
        this.posAmount = posAmount;
    }

    public BigDecimal getPosNum() {
        return posNum;
    }

    public void setPosNum(BigDecimal posNum) {
        this.posNum = posNum;
    }

    public BigDecimal getBranchAmount() {
        return branchAmount;
    }

    public void setBranchAmount(BigDecimal branchAmount) {
        this.branchAmount = branchAmount;
    }

    public BigDecimal getBranchNum() {
        return branchNum;
    }

    public void setBranchNum(BigDecimal branchNum) {
        this.branchNum = branchNum;
    }

    public BigDecimal getCompareAmount() {
        return compareAmount;
    }

    public void setCompareAmount(BigDecimal compareAmount) {
        this.compareAmount = compareAmount;
    }

    public BigDecimal getCompareNum() {
        return compareNum;
    }

    public void setCompareNum(BigDecimal compareNum) {
        this.compareNum = compareNum;
    }

    public BigDecimal getBranchCompareAmount() {
        return branchCompareAmount;
    }

    public void setBranchCompareAmount(BigDecimal branchCompareAmount) {
        this.branchCompareAmount = branchCompareAmount;
    }

    public BigDecimal getBranchCompareNum() {
        return branchCompareNum;
    }

    public void setBranchCompareNum(BigDecimal branchCompareNum) {
        this.branchCompareNum = branchCompareNum;
    }
}
