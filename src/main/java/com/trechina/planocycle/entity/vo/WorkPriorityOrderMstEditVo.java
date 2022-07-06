package com.trechina.planocycle.entity.vo;

public class WorkPriorityOrderMstEditVo {


    private Integer productPowerCd;

    private Integer shelfCd;

    private Long shelfPatternCd;

    private Short partitionFlag;

    private Short partitionVal;

    private String commonPartsData;

    private Integer topPartitionVal;
    private Integer topPartitionFlag;
    private Integer tanaWidCheck;

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public Integer getShelfCd() {
        return shelfCd;
    }

    public void setShelfCd(Integer shelfCd) {
        this.shelfCd = shelfCd;
    }

    public Long getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Long shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
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

    public String getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(String commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    public Integer getTopPartitionVal() {
        return topPartitionVal;
    }

    public void setTopPartitionVal(Integer topPartitionVal) {
        this.topPartitionVal = topPartitionVal;
    }

    public Integer getTopPartitionFlag() {
        return topPartitionFlag;
    }

    public void setTopPartitionFlag(Integer topPartitionFlag) {
        this.topPartitionFlag = topPartitionFlag;
    }

    public Integer getTanaWidCheck() {
        return tanaWidCheck;
    }

    public void setTanaWidCheck(Integer tanaWidCheck) {
        this.tanaWidCheck = tanaWidCheck;
    }

    @Override
    public String toString() {
        return "WorkPriorityOrderMstEditVo{" +
                "productPowerCd=" + productPowerCd +
                ", shelfCd=" + shelfCd +
                ", shelfPatternCd=" + shelfPatternCd +
                ", partitionFlag=" + partitionFlag +
                ", partitionVal=" + partitionVal +
                ", commonPartsData='" + commonPartsData + '\'' +
                ", topPartitionVal=" + topPartitionVal +
                ", topPartitionFlag=" + topPartitionFlag +
                ", tanaWidCheck=" + tanaWidCheck +
                '}';
    }
}