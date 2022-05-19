package com.trechina.planocycle.entity.po;


import com.alibaba.fastjson.JSONObject;

public class ProductPowerParam {
    private String company;
    private JSONObject customerCondition;
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
    private String channelNm;
    private String placeNm;
    private String project;
    private String shelfPatternCd;
    private Integer janName2colNum;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public void setRankWeight(String RankWeight) {
        this.rankWeight = RankWeight;
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

    public JSONObject getCustomerCondition() {
        return customerCondition;
    }

    public void setCustomerCondition(JSONObject customerCondition) {

        this.customerCondition = customerCondition;
    }

    public String getChannelNm() {
        return channelNm;
    }

    public void setChannelNm(String channelNm) {
        this.channelNm = channelNm;
    }

    public String getPlaceNm() {
        return placeNm;
    }

    public void setPlaceNm(String placeNm) {
        this.placeNm = placeNm;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(String shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public Integer getJanName2colNum() {
        return janName2colNum;
    }

    public void setJanName2colNum(Integer janName2colNum) {
        this.janName2colNum = janName2colNum;
    }

    @Override
    public String toString() {
        return "ProductPowerParam{" +
                "company='" + company + '\'' +
                ", customerCondition=" + customerCondition +
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
                ", channelNm='" + channelNm + '\'' +
                ", placeNm='" + placeNm + '\'' +
                ", project='" + project + '\'' +
                ", shelfPatternCd='" + shelfPatternCd + '\'' +
                ", janName2colNum=" + janName2colNum +
                '}';
    }
}
