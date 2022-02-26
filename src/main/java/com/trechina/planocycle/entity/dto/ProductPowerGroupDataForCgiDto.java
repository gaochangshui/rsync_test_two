package com.trechina.planocycle.entity.dto;

import java.util.Map;

public class ProductPowerGroupDataForCgiDto {
    private String mode;
    private String company;
    private String guid;
    private Integer productPowerNo;
    private String recentlyEndTime;
    private String recentlyFlag;
    private String recentlyStTime;
    private String seasonEndTime;
    private String seasonFlag;
    private String seasonStTime;
    private String prdCd;
    private String storeCd;
    private String yearFlag;
    private Map<Object,Object> customerCondition;
    private String change_flag;
    private String usercd;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getProductPowerNo() {
        return productPowerNo;
    }

    public void setProductPowerNo(Integer productPowerNo) {
        this.productPowerNo = productPowerNo;
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

    public String getPrdCd() {
        return prdCd;
    }

    public void setPrdCd(String prdCd) {
        this.prdCd = prdCd;
    }

    public String getStoreCd() {
        return storeCd;
    }

    public void setStoreCd(String storeCd) {
        this.storeCd = storeCd;
    }


    public Map<Object, Object> getCustomerCondition() {
        return customerCondition;
    }

    public void setCustomerCondition(Map<Object, Object> customerCondition) {
        this.customerCondition = customerCondition;
    }

    public String getChange_flag() {
        return change_flag;
    }

    public void setChange_flag(String change_flag) {
        this.change_flag = change_flag;
    }

    public String getUsercd() {
        return usercd;
    }

    public void setUsercd(String usercd) {
        this.usercd = usercd;
    }

    public String getYearFlag() {
        return yearFlag;
    }

    public void setYearFlag(String yearFlag) {
        this.yearFlag = yearFlag;
    }

    @Override
    public String toString() {
        return "ProductPowerGroupDataForCgiDto{" +
                "mode='" + mode + '\'' +
                ", company='" + company + '\'' +
                ", guid='" + guid + '\'' +
                ", productPowerNo=" + productPowerNo +
                ", recentlyEndTime='" + recentlyEndTime + '\'' +
                ", recentlyFlag='" + recentlyFlag + '\'' +
                ", recentlyStTime='" + recentlyStTime + '\'' +
                ", seasonEndTime='" + seasonEndTime + '\'' +
                ", seasonFlag='" + seasonFlag + '\'' +
                ", seasonStTime='" + seasonStTime + '\'' +
                ", prdCd='" + prdCd + '\'' +
                ", storeCd='" + storeCd + '\'' +
                ", yearFlag='" + yearFlag + '\'' +
                ", customerCondition='" + customerCondition + '\'' +
                ", change_flag='" + change_flag + '\'' +
                ", usercd='" + usercd + '\'' +
                '}';
    }
}
