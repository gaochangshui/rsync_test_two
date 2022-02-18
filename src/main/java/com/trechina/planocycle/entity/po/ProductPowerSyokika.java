package com.trechina.planocycle.entity.po;


public class ProductPowerSyokika {
    private String companyCd;
    private Integer priorityOrderCd;
    private String jan;
    private String skuName;
    private String classifyBig;
    private String classifyMiddle;
    private String classifySmall;
    private String classifyFine;
    private Double posAmount;
    private Double posNum;
    private Double branchAmount;
    private Double branchNum;
    private Double compareAmount;
    private Double compareNum;
    private Double branchCompareAmount;
    private Double branchCompareNum;

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

    public Double getPosAmount() {
        return posAmount;
    }

    public void setPosAmount(Double posAmount) {
        this.posAmount = posAmount;
    }

    public Double getPosNum() {
        return posNum;
    }

    public void setPosNum(Double posNum) {
        this.posNum = posNum;
    }

    public Double getBranchAmount() {
        return branchAmount;
    }

    public void setBranchAmount(Double branchAmount) {
        this.branchAmount = branchAmount;
    }

    public Double getBranchNum() {
        return branchNum;
    }

    public void setBranchNum(Double branchNum) {
        this.branchNum = branchNum;
    }

    public Double getCompareAmount() {
        return compareAmount;
    }

    public void setCompareAmount(Double compareAmount) {
        this.compareAmount = compareAmount;
    }

    public Double getCompareNum() {
        return compareNum;
    }

    public void setCompareNum(Double compareNum) {
        this.compareNum = compareNum;
    }

    public Double getBranchCompareAmount() {
        return branchCompareAmount;
    }

    public void setBranchCompareAmount(Double branchCompareAmount) {
        this.branchCompareAmount = branchCompareAmount;
    }

    public Double getBranchCompareNum() {
        return branchCompareNum;
    }

    public void setBranchCompareNum(Double branchCompareNum) {
        this.branchCompareNum = branchCompareNum;
    }

    @Override
    public String toString() {
        return "ProductPowerSyokika{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", jan='" + jan + '\'' +
                ", skuName='" + skuName + '\'' +
                ", classifyBig='" + classifyBig + '\'' +
                ", classifyMiddle='" + classifyMiddle + '\'' +
                ", classifySmall='" + classifySmall + '\'' +
                ", classifyFine='" + classifyFine + '\'' +
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
