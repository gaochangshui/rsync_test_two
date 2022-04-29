package com.trechina.planocycle.entity.vo;

public class WorkPriorityOrderMstEditVo {


    private Integer productPowerCd;

    private Integer shelfCd;

    private Long shelfPatternCd;

//    private String areaNameCd;

    private Short attrA;

    private Short attrB;

    private Short partitionFlag;

    private Short partitionVal;

    public Integer getShelfCd() {
        return shelfCd;
    }

    public void setShelfCd(Integer shelfCd) {
        this.shelfCd = shelfCd;
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

    public Short getAttrA() {
        return attrA;
    }

    public void setAttrA(Short attrA) {
        this.attrA = attrA;
    }

    public Short getAttrB() {
        return attrB;
    }

    public void setAttrB(Short attrB) {
        this.attrB = attrB;
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

    @Override
    public String toString() {
        return "WorkPriorityOrderMstEditVo{" +
                "productPowerCd=" + productPowerCd +
                ", shelfCd=" + shelfCd +
                ", shelfPatternCd=" + shelfPatternCd +
                ", attrA=" + attrA +
                ", attrB=" + attrB +
                ", partitionFlag=" + partitionFlag +
                ", partitionVal=" + partitionVal +
                '}';
    }
}