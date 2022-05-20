package com.trechina.planocycle.entity.dto;

public class CommonPartsDto {
    private String coreCompany;
    private String prodIsCore;
    private String prodMstClass;

    public String getCoreCompany() {
        return coreCompany;
    }

    public void setCoreCompany(String coreCompany) {
        this.coreCompany = coreCompany;
    }

    public String getProdIsCore() {
        return prodIsCore;
    }

    public void setProdIsCore(String prodIsCore) {
        this.prodIsCore = prodIsCore;
    }

    public String getProdMstClass() {
        return prodMstClass;
    }

    public void setProdMstClass(String prodMstClass) {
        this.prodMstClass = prodMstClass;
    }
}
