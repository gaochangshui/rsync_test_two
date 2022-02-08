package com.trechina.planocycle.entity.dto;

import java.util.Arrays;
import java.util.List;

public class PriorityOrderDataForCgiDto {
    private String company;
    private String shelfPatternNo;
    private String guid;
    private String mode;
    private Integer productPowerNo;
    private String attributeCd;
    private Integer productNmFlag;
    private Integer priorityNO;
    private String writeReadFlag;
    private String[] dataArray;
    private List<String> orderCol;
    private Integer itemFlg;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getShelfPatternNo() {
        return shelfPatternNo;
    }

    public void setShelfPatternNo(String shelfPatternNo) {
        this.shelfPatternNo = shelfPatternNo;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getProductPowerNo() {
        return productPowerNo;
    }

    public void setProductPowerNo(Integer productPowerNo) {
        this.productPowerNo = productPowerNo;
    }

    public String getAttributeCd() {
        return attributeCd;
    }

    public void setAttributeCd(String attributeCd) {
        this.attributeCd = attributeCd;
    }

    public Integer getProductNmFlag() {
        return productNmFlag;
    }

    public void setProductNmFlag(Integer productNmFlag) {
        this.productNmFlag = productNmFlag;
    }

    public Integer getPriorityNO() {
        return priorityNO;
    }

    public void setPriorityNO(Integer priorityNO) {
        this.priorityNO = priorityNO;
    }

    public String getWriteReadFlag() {
        return writeReadFlag;
    }

    public void setWriteReadFlag(String writeReadFlag) {
        this.writeReadFlag = writeReadFlag;
    }


    public String[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(String[] dataArray) {
        this.dataArray = dataArray;
    }

    public List<String> getOrderCol() {
        return orderCol;
    }

    public void setOrderCol(List<String> orderCol) {
        this.orderCol = orderCol;
    }

    public Integer getItemFlg() {
        return itemFlg;
    }

    public void setItemFlg(Integer itemFlg) {
        this.itemFlg = itemFlg;
    }

    @Override
    public String toString() {
        return "PriorityOrderDataForCgiDto{" +
                "company='" + company + '\'' +
                ", shelfPatternNo='" + shelfPatternNo + '\'' +
                ", guid='" + guid + '\'' +
                ", mode='" + mode + '\'' +
                ", productPowerNo=" + productPowerNo +
                ", attributeCd='" + attributeCd + '\'' +
                ", productNmFlag=" + productNmFlag +
                ", priorityNO=" + priorityNO +
                ", writeReadFlag='" + writeReadFlag + '\'' +
                ", dataArray=" + Arrays.toString(dataArray) +
                ", orderCol=" + orderCol +
                ", itemFlg=" + itemFlg +
                '}';
    }
}
