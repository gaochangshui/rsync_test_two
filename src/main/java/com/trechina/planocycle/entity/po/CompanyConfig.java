package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class CompanyConfig {
    private String companyCd;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

}
