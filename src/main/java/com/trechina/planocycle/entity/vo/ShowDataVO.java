package com.trechina.planocycle.entity.vo;

public class ShowDataVO {
    Integer productPowerCd;
    String companyCd;
    String[] posCd;
    String[] prepareCd;
    String[] intageCd;
    String[] customerCd;

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String[] getPosCd() {
        return posCd;
    }

    public void setPosCd(String[] posCd) {
        this.posCd = posCd;
    }

    public String[] getPrepareCd() {
        return prepareCd;
    }

    public void setPrepareCd(String[] prepareCd) {
        this.prepareCd = prepareCd;
    }

    public String[] getIntageCd() {
        return intageCd;
    }

    public void setIntageCd(String[] intageCd) {
        this.intageCd = intageCd;
    }

    public String[] getCustomerCd() {
        return customerCd;
    }

    public void setCustomerCd(String[] customerCd) {
        this.customerCd = customerCd;
    }
}
