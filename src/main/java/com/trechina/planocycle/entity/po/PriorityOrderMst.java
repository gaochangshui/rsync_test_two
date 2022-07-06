package com.trechina.planocycle.entity.po;

public class PriorityOrderMst {
    private String companyCd;

    private Integer priorityOrderCd;

    private String priorityOrderName;

    private Integer productPowerCd;

    private Short partitionFlag;

    private Short partitionVal;

    private Short topPartitionFlag;

    private Short topPartitionVal;
    private String attrOption;

    private String commonPartsData;

    private Integer tanaWidCheck;

    public String getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(String commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    public Integer getTanaWidCheck() {
        return tanaWidCheck;
    }

    public void setTanaWidCheck(Integer tanaWidCheck) {
        this.tanaWidCheck = tanaWidCheck;
    }

    public Short getTopPartitionFlag() {
        return topPartitionFlag;
    }

    public void setTopPartitionFlag(Short topPartitionFlag) {
        this.topPartitionFlag = topPartitionFlag;
    }

    public Short getTopPartitionVal() {
        return topPartitionVal;
    }

    public void setTopPartitionVal(Short topPartitionVal) {
        this.topPartitionVal = topPartitionVal;
    }

    public String getAttrOption() {
        return attrOption;
    }

    public void setAttrOption(String attrOption) {
        this.attrOption = attrOption;
    }

    public Short getPartitionFlag() {
        return partitionFlag;
    }

    public void setPartitionFlag(Short partitionFlag) {
        this.partitionFlag = partitionFlag;
    }

    public Short getPartitionVal() {
        return partitionVal;
    }

    public void setPartitionVal(Short partitionVal) {
        this.partitionVal = partitionVal;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
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
        this.priorityOrderName = priorityOrderName == null ? null : priorityOrderName.trim();
    }

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    @Override
    public String toString() {
        return "PriorityOrderMst{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", priorityOrderName='" + priorityOrderName + '\'' +
                ", productPowerCd=" + productPowerCd +
                ", partitionFlag=" + partitionFlag +
                ", partitionVal=" + partitionVal +
                ", attrOption='" + attrOption + '\'' +
                '}';
    }
}
