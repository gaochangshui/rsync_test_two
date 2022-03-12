package com.trechina.planocycle.entity.dto;

import java.util.Arrays;

public class PriorityOrderJanCgiDto {
    private String company;
    private Integer shelfPatternNo;
    private String guid;
    private String mode;
    private String[] dataArray;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getShelfPatternNo() {
        return shelfPatternNo;
    }

    public void setShelfPatternNo(Integer shelfPatternNo) {
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

    public String[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(String[] dataArray) {
        this.dataArray = dataArray;
    }

    @Override
    public String toString() {
        return "PriorityOrderJanCgiDto{" +
                "company='" + company + '\'' +
                ", shelfPatternNo=" + shelfPatternNo +
                ", guid='" + guid + '\'' +
                ", mode='" + mode + '\'' +
                ", dataArray=" + Arrays.toString(dataArray) +
                '}';
    }
}
