package com.trechina.planocycle.entity.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyListDto {

    private String F1 ;

    private String F2 ;

    private String F3 ;

    private String F4 ;
    @JsonProperty(value = "F1")
    public String getF1() {
        return F1;
    }

    public void setF1(String f1) {
        F1 = f1;
    }
    @JsonProperty("F2")
    public String getF2() {
        return F2;
    }

    public void setF2(String f2) {
        F2 = f2;
    }
    @JsonProperty("F3")
    public String getF3() {
        return F3;
    }

    public void setF3(String f3) {
        F3 = f3;
    }
    @JsonProperty("F4")
    public String getF4() {
        return F4;
    }

    public void setF4(String f4) {
        F4 = f4;
    }

    @Override
    public String toString() {
        return "CompanyListDto{" +
                "F1='" + F1 + '\'' +
                ", F2='" + F2 + '\'' +
                ", F3='" + F3 + '\'' +
                ", F4='" + F4 + '\'' +
                '}';
    }
}
