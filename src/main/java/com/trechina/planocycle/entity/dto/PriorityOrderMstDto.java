package com.trechina.planocycle.entity.dto;


public class PriorityOrderMstDto {
    private String companyCd;

    private Integer priorityOrderCd;

    private String priorityOrderName;

    private Integer productPowerCd;

    private String shelfPatternCd;

    private String priorityData;

    private String attributeCd;

    private String rankAttributeList;

    private String attrOption;

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

    public String getPriorityOrderName() {
        return priorityOrderName;
    }

    public void setPriorityOrderName(String priorityOrderName) {
        this.priorityOrderName = priorityOrderName;
    }

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public String getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(String shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public String getPriorityData() {
        return priorityData;
    }

    public void setPriorityData(String priorityData) {
        this.priorityData = priorityData;
    }

    public String getAttributeCd() {
        return attributeCd;
    }

    public void setAttributeCd(String attributeCd) {
        this.attributeCd = attributeCd;
    }

    public String getAttrOption() {
        return attrOption;
    }

    public void setAttrOption(String attrOption) {
        this.attrOption = attrOption;
    }

    public String getRankAttributeList() {
        return rankAttributeList;
    }

    public void setRankAttributeList(String rankAttributeList) {
        this.rankAttributeList = rankAttributeList;
    }

    @Override
    public String toString() {
        return "PriorityOrderMstDto{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", priorityOrderName='" + priorityOrderName + '\'' +
                ", productPowerCd=" + productPowerCd +
                ", shelfPatternCd='" + shelfPatternCd + '\'' +
                ", priorityData='" + priorityData + '\'' +
                ", attributeCd='" + attributeCd + '\'' +
                ", rankAttributeList='" + rankAttributeList + '\'' +
                ", attrOption='" + attrOption + '\'' +
                '}';
    }
}
