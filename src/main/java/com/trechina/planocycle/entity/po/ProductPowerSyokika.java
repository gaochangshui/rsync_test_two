package com.trechina.planocycle.entity.po;


public class ProductPowerSyokika {
    private String companyCd;
    private String productPowerCd;
    private String jan;
    private String skuName;
    private String classifyBig;
    private String classifyMiddle;
    private String classifySmall;
    private String classifyFine;
    // POS
    private String posAmount;
    private Integer posNum;
    private String branchAmount;
    private Integer branchNum;
    private String compareAmount;
    private Integer compareNum;
    private String branchCompareAmount;
    private Integer branchCompareNum;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(String productPowerCd) {
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

    public String getPosAmount() {
        return posAmount;
    }

    public void setPosAmount(String posAmount) {
        this.posAmount = posAmount;
    }

    public Integer getPosNum() {
        return posNum;
    }

    public void setPosNum(Integer posNum) {
        this.posNum = posNum;
    }

    public String getBranchAmount() {
        return branchAmount;
    }

    public void setBranchAmount(String branchAmount) {
        this.branchAmount = branchAmount;
    }

    public Integer getBranchNum() {
        return branchNum;
    }

    public void setBranchNum(Integer branchNum) {
        this.branchNum = branchNum;
    }

    public String getCompareAmount() {
        return compareAmount;
    }

    public void setCompareAmount(String compareAmount) {
        this.compareAmount = compareAmount;
    }

    public Integer getCompareNum() {
        return compareNum;
    }

    public void setCompareNum(Integer compareNum) {
        this.compareNum = compareNum;
    }

    public String getBranchCompareAmount() {
        return branchCompareAmount;
    }

    public void setBranchCompareAmount(String branchCompareAmount) {
        this.branchCompareAmount = branchCompareAmount;
    }

    public Integer getBranchCompareNum() {
        return branchCompareNum;
    }

    public void setBranchCompareNum(Integer branchCompareNum) {
        this.branchCompareNum = branchCompareNum;
    }

    @Override
    public String toString() {
        return "ProductPowerSyokika{" +
                "companyCd='" + companyCd + '\'' +
                ", productPowerCd='" + productPowerCd + '\'' +
                ", jan='" + jan + '\'' +
                ", skuName='" + skuName + '\'' +
                ", classifyBig='" + classifyBig + '\'' +
                ", classifyMiddle='" + classifyMiddle + '\'' +
                ", classifySmall='" + classifySmall + '\'' +
                ", classifyFine='" + classifyFine + '\'' +
                ", posAmount='" + posAmount + '\'' +
                ", posNum=" + posNum +
                ", branchAmount='" + branchAmount + '\'' +
                ", branchNum=" + branchNum +
                ", compareAmount='" + compareAmount + '\'' +
                ", compareNum=" + compareNum +
                ", branchCompareAmount='" + branchCompareAmount + '\'' +
                ", branchCompareNum=" + branchCompareNum +
                '}';
    }
}
