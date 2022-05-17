package com.trechina.planocycle.entity.po;


public class ProductPowerParamVo {
    private String company;
    private String customerCondition;
    private String prdCd;
    private Integer productPowerNo;
    private String rankWeight;
    private String recentlyEndTime;
    private String recentlyFlag;
    private String recentlyStTime;
    private String seasonEndTime;
    private String seasonFlag;
    private String seasonStTime;
    private String storeCd;
    private String yearFlag;
    private String commonPartsData;
    private String project;
    private Integer shelfPatternCd;


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCustomerCondition() {
        return customerCondition;
    }

    public void setCustomerCondition(String customerCondition) {
        this.customerCondition = customerCondition;
    }

    public String getPrdCd() {
        return prdCd;
    }

    public void setPrdCd(String prdCd) {
        this.prdCd = prdCd;
    }

    public Integer getProductPowerNo() {
        return productPowerNo;
    }

    public void setProductPowerNo(Integer productPowerNo) {
        this.productPowerNo = productPowerNo;
    }

    public String getRankWeight() {
        return rankWeight;
    }

    public void setRankWeight(String rankWeight) {
        this.rankWeight = rankWeight;
    }

    public String getRecentlyEndTime() {
        return recentlyEndTime;
    }

    public void setRecentlyEndTime(String recentlyEndTime) {
        this.recentlyEndTime = recentlyEndTime;
    }

    public String getRecentlyFlag() {
        return recentlyFlag;
    }

    public void setRecentlyFlag(String recentlyFlag) {
        this.recentlyFlag = recentlyFlag;
    }

    public String getRecentlyStTime() {
        return recentlyStTime;
    }

    public void setRecentlyStTime(String recentlyStTime) {
        this.recentlyStTime = recentlyStTime;
    }

    public String getSeasonEndTime() {
        return seasonEndTime;
    }

    public void setSeasonEndTime(String seasonEndTime) {
        this.seasonEndTime = seasonEndTime;
    }

    public String getSeasonFlag() {
        return seasonFlag;
    }

    public void setSeasonFlag(String seasonFlag) {
        this.seasonFlag = seasonFlag;
    }

    public String getSeasonStTime() {
        return seasonStTime;
    }

    public void setSeasonStTime(String seasonStTime) {
        this.seasonStTime = seasonStTime;
    }

    public String getStoreCd() {
        return storeCd;
    }

    public void setStoreCd(String storeCd) {
        this.storeCd = storeCd;
    }

    public String getYearFlag() {
        return yearFlag;
    }

    public void setYearFlag(String yearFlag) {
        this.yearFlag = yearFlag;
    }

    public String getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(String commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    @Override
    public String toString() {
        return "ProductPowerParamVo{" +
                "company='" + company + '\'' +
                ", customerCondition='" + customerCondition + '\'' +
                ", prdCd='" + prdCd + '\'' +
                ", productPowerNo=" + productPowerNo +
                ", rankWeight='" + rankWeight + '\'' +
                ", recentlyEndTime='" + recentlyEndTime + '\'' +
                ", recentlyFlag='" + recentlyFlag + '\'' +
                ", recentlyStTime='" + recentlyStTime + '\'' +
                ", seasonEndTime='" + seasonEndTime + '\'' +
                ", seasonFlag='" + seasonFlag + '\'' +
                ", seasonStTime='" + seasonStTime + '\'' +
                ", storeCd='" + storeCd + '\'' +
                ", yearFlag='" + yearFlag + '\'' +
                ", commonPartsData='" + commonPartsData + '\'' +
                ", project='" + project + '\'' +
                ", shelfPatternCd=" + shelfPatternCd +
                '}';
    }
}
