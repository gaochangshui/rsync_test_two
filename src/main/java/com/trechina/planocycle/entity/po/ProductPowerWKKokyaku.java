package com.trechina.planocycle.entity.po;

import java.math.BigDecimal;

/**
 * 顾客group
 *
 */
public class ProductPowerWKKokyaku {
    private String jan;
    private String skuName;
    private BigDecimal posAmount;
    private BigDecimal posNum;
    private BigDecimal branchAmount;
    private BigDecimal branchNum;
    private BigDecimal compareAmount;
    private BigDecimal compareNum;
    private BigDecimal branchCompareAmount;
    private BigDecimal branchCompareNum;




    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
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

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    @Override
    public String toString() {
        return "ProductPowerWKKokyaku{" +
                ", jan='" + jan + '\'' +
                ", skuName='" + skuName + '\'' +
                ", posAmount=" + posAmount +
                ", posNum=" + posNum +
                ", branchAmount=" + branchAmount +
                ", branchNum=" + branchNum +
                ", compareAmount=" + compareAmount +
                ", compareNum=" + compareNum +
                ", branchCompareAmount=" + branchCompareAmount +
                ", branchCompareNum=" + branchCompareNum +
                '}';
    }
}
