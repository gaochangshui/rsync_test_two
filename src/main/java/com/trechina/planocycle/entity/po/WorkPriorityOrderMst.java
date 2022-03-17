package com.trechina.planocycle.entity.po;

public class WorkPriorityOrderMst {
    private String companyCd;

    private String authorCd;

    private Integer priorityOrderCd;

    private Integer productPowerCd;

    private Long shelfPatternCd;

    private Short attribute1;

    private Short attribute2;

    private Short partitionFlag;

    private Short partitionVal;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd == null ? null : authorCd.trim();
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public Long getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Long shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public Short getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(Short attribute1) {
        this.attribute1 = attribute1;
    }

    public Short getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(Short attribute2) {
        this.attribute2 = attribute2;
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
}