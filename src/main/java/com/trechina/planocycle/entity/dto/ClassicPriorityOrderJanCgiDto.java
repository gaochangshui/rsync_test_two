package com.trechina.planocycle.entity.dto;

import java.util.Arrays;

public class ClassicPriorityOrderJanCgiDto {
    private String company;
    private String guid;
    private String mode;
    private String[] dataArray;
    private String usercd;
    private String attributeCd;
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

    public String getUsercd() {
        return usercd;
    }

    public void setUsercd(String usercd) {
        this.usercd = usercd;
    }

    public String getAttributeCd() {
        return attributeCd;
    }

    public void setAttributeCd(String attributeCd) {
        this.attributeCd = attributeCd;
    }

    @Override
    public String toString() {
        return "PriorityOrderJanCgiDto{" +
                "company='" + company + '\'' +
                ", guid='" + guid + '\'' +
                ", mode='" + mode + '\'' +
                ", dataArray=" + Arrays.toString(dataArray) +
                ", usercd='" + usercd + '\'' +
                ", attributeCd='" + attributeCd + '\'' +
                '}';
    }
}
