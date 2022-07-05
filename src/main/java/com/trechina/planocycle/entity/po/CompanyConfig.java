package com.trechina.planocycle.entity.po;

public class CompanyConfig {
    private String companyCd;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    @Override
    public String toString() {
        return "CompanyConfig{" +
                "companyCd='" + companyCd + '\'' +
                '}';
    }
}
